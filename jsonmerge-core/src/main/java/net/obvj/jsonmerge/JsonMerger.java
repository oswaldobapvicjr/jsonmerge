/*
 * Copyright 2022 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.jsonmerge;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.jsonmerge.provider.JsonProvider;
import net.obvj.jsonmerge.util.JsonPathExpression;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;

/**
 * Combines two JSON documents.
 * <p>
 * The operation is provider-agnostic and relies on a specialized {@link JsonProvider}
 * which must be specified via constructor.
 * <p>
 * The resulting JSON document from the merge operation shall contain all exclusive
 * objects from source documents. And in case of key collision (i.e., the same key appears
 * in both documents), the following rules will be applied:
 * <ul>
 * <li>For simple values, such as strings, numbers and boolean values, the value from the
 * highest-precedence JSON document will be selected;</li>
 * <li>If the value is a JSON object in <b>both</b> documents, the two objects will be
 * merged recursively;</li>
 * <li>If the value is a JSON array in <b>both</b> documents, then all <b>distinct</b>
 * elements (i.e., not repeated ones) will be copied to the resulting array, unless a
 * different {@link JsonMergeOption} is provided specifically for that array path;</li>
 * <li>If the types are <b>incompatible</b> in the source JSON documents (e.g.: array in
 * one side and simple value or complex object in the other), then a copy of the object
 * from the highest-precedence document will be selected as fallback</li>
 * </ul>
 * <p>
 * <b>Note: </b> The first JSON document passed is always considered to have higher
 * precedence than the second one in {@link #merge(T, T)}.
 * <p>
 * For advanced merge options, refer to {@link JsonMergeOption}.
 *
 * @param <T> the type that represents the actual JSON document at the specified
 *            {@link JsonProvider}
 *
 * @see JsonProvider
 * @see JsonMergeOption
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 */
public class JsonMerger<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMerger.class);

    private final JsonProvider jsonProvider;

    /**
     * Creates a new JSON Merger for a specific provider.
     *
     * @param jsonProvider the {@link JsonProvider} to use; not {@code null}
     * @throws NullPointerException if the specified JsonProvider is null
     */
    public JsonMerger(JsonProvider jsonProvider)
    {
        this.jsonProvider = requireNonNull(jsonProvider, "The JsonProvider cannot be null");
    }

    private static Map<JsonPathExpression, JsonMergeOption> parseMergeOptions(
            JsonMergeOption[] mergeOptions)
    {
        return stream(mergeOptions)
                .collect(toMap(JsonMergeOption::getPath, Function.identity()));
    }

    /**
     * Combines two JSON documents.
     *
     * @param json1        the first JSON document; this object will have higher precedence
     *                     than the other one in case of key collision
     * @param json2        the second JSON document (lower precedence object)
     * @param mergeOptions an array of options on how to merge the documents (optional)
     * @return a new JSON document from the combination of {@code json1} and {@code json2}
     */
    public T merge(T json1, T json2, JsonMergeOption... mergeOptions)
    {
        Map<JsonPathExpression, JsonMergeOption> options = parseMergeOptions(mergeOptions);
        JsonPartMerger merger = new JsonPartMerger(jsonProvider, JsonPathExpression.ROOT, options);
        LOGGER.info("Merging JSON documents...");

        Stopwatch stopwatch = Stopwatch.createStarted(Type.WALL_CLOCK_TIME);
        T result = (T) merger.merge(json1, json2);

        LOGGER.info("Operation finished in {}", stopwatch.elapsedTime());
        return result;
    }

    private static class JsonPartMerger
    {
        private final JsonProvider jsonProvider;
        private final JsonPathExpression absolutePath;
        private final Map<JsonPathExpression, JsonMergeOption> options;


        /**
         * Creates a new {@link JsonPartMerger} for an absolute path.
         *
         * @param jsonProvider the {@link JsonProvider} to use; not {@code null}
         * @param absolutePath the absolute path of the current JSON object or array inside the
         *                     root JSON object}; not {@code null}
         * @param options      a map storing custom merge options by path; not {@code null}
         */
        private JsonPartMerger(JsonProvider jsonProvider, JsonPathExpression absolutePath,
                Map<JsonPathExpression, JsonMergeOption> options)
        {
            this.jsonProvider = requireNonNull(jsonProvider, "The JsonProvider cannot be null");
            this.absolutePath = absolutePath;
            this.options = options;
        }

        /**
         * Merges two JSON objects.
         *
         * @param json1 the highest-precedence JSON object
         * @param json2 the lowest-precedence JSON object
         * @return a combination of the two JSON objects
         */
        private Object merge(Object json1, Object json2)
        {
            LOGGER.debug("Merging object on path: {}", absolutePath);

            if (jsonProvider.isEmpty(json2))
            {
                return jsonProvider.newJsonObject(json1);
            }

            Object result = jsonProvider.newJsonObject();

            // First iterate through the first json
            for (Entry<String, Object> entry : jsonProvider.entrySet(json1))
            {
                String key = entry.getKey();
                Object value1 = entry.getValue();
                Object value2 = jsonProvider.get(json2, key);

                if (jsonProvider.isJsonObject(value1))
                {
                    JsonPartMerger merger = new JsonPartMerger(jsonProvider,
                            absolutePath.appendChild(key), options);
                    jsonProvider.put(result, key, merger.mergeSafely(value1, value2));
                }
                else if (jsonProvider.isJsonArray(value1))
                {
                    JsonPartMerger merger = new JsonPartMerger(jsonProvider,
                            absolutePath.appendChild(key), options);
                    jsonProvider.put(result, key, merger.mergeArray(value1, value2));
                }
                else
                {
                    jsonProvider.put(result, key, value1); // Get from the highest-precedence json
                }
            }

            // Then iterate through the second json to find additional keys
            for (Entry<String, Object> entry : jsonProvider.entrySet(json2))
            {
                jsonProvider.putIfAbsent(result, entry.getKey(), entry.getValue());
            }

            LOGGER.debug("Merge completed for {}", absolutePath);
            return result;
        }

        /**
         * Support method for type-safety during the merge of a JSON object.
         *
         * @param json   the highest-precedence JSON object to be merged
         * @param object the lowest-precedence object to be merged
         * @return a combination of the two objects, provided that the second object is a JSON
         *         object too
         */
        private Object mergeSafely(Object json, Object object)
        {
            if (jsonProvider.isJsonObject(object))
            {
                return merge(json, object);
            }
            // The second object is either null or has an incompatible type,
            // so simply assume object with the highest precedence.
            // Create a copy of it for safety.
            LOGGER.warn(
                    "Incompatible types on path: {}. Selecting the object with higher precedence...",
                    absolutePath);
            return jsonProvider.newJsonObject(json);
        }

        /**
         * Support method for type-safety during the merge a JSON array.
         *
         * @param array  the highest-precedence JSON array to be merged
         * @param object the lowest-precedence object to be merged
         * @return a combination of the two objects, provided that the second object is a JSON
         *         array too
         */
        private Object mergeArray(Object array, Object object)
        {
            LOGGER.debug("Merging array on path: {}", absolutePath);
            if (jsonProvider.isJsonArray(object))
            {
                Object result = mergeArraySafely(array, object);
                LOGGER.debug("Merge completed for {}", absolutePath);
                return result;
            }
            // The second object is either null or has an incompatible type,
            // so simply assume the array from the highest-precedence object.
            // Create a copy of it for safety.
            LOGGER.warn(
                    "Incompatible types on path: {}. Selecting the object with higher precedence...",
                    absolutePath);
            return jsonProvider.newJsonArray(array);
        }

        /**
         * Merges two JSON array instances.
         *
         * @param array1 the highest-precedence JSON array
         * @param array2 the lowest-precedence JSON array
         * @return a combination of the two JSON arrays
         */
        private Object mergeArraySafely(Object array1, Object array2)
        {
            // The 1st array is always the highest-precedence one
            Object result = jsonProvider.newJsonArray(array1);

            JsonMergeOption pathOption = getMergeOption();
            List<String> keys = pathOption.getKeys();
            if (!keys.isEmpty())
            {
                if (LOGGER.isDebugEnabled())
                {
                    int size = keys.size();
                    LOGGER.debug("Checking distinct objects inside {} with {} {}: {}",
                            absolutePath, size, size == 1 ? "key" : "keys", keys);
                }

                // Here we add objects from the 2nd array only if they are not present in the
                // 1st one. Because the user specified a distinct key, then use it to find the
                // "equal" objects.
                jsonProvider.forEachElementInArray(array2, object ->
                        addDistinctObjectToArray(object, keys, result, pathOption));
            }
            else
            {
                jsonProvider.forEachElementInArray(array2, object ->
                        addObjectToArray(result, object, pathOption));
            }

            return result;
        }

        /**
         * @return the {@link JsonMergeOption} associated with the current path, or a default
         *         option; not null
         */
        private JsonMergeOption getMergeOption()
        {
            return options.getOrDefault(absolutePath, JsonMergeOption.DEFAULT);
        }

        private void addObjectToArray(Object array, Object object, JsonMergeOption pathOption)
        {
            addObjectToArray(array, object, pathOption.isDistinctObjectsOnly());
        }

        private void addDistinctObjectToArray(Object array, Object object)
        {
            addObjectToArray(array, object, true);
        }

        private void addObjectToArray(Object array, Object object, boolean distinctObjectsOnly)
        {
            if (distinctObjectsOnly && jsonProvider.arrayContains(array, object))
            {
                return;
            }
            jsonProvider.add(array, object);
        }

        /**
         * Adds a distinct object, identified by a specific key, to the target array.
         *
         * @param object     the object to be added, given it is not already is the {@code array}
         * @param keys       a list of distinctive keys inside the JSON object to be used for
         *                   "equal" objects identification in the target array
         * @param array      the array to which the object will be added, provided that it
         *                   contains no other object with the same value for the specified
         *                   {@code key}
         * @param pathOption a {@link JsonMergeOption} for the current path
         */
        private void addDistinctObjectToArray(Object object, List<String> keys, Object array,
                JsonMergeOption pathOption)
        {
            if (jsonProvider.isJsonObject(object))
            {
                Map<String, Object> values = new HashMap<>();
                for (String key : keys)
                {
                    Object value = jsonProvider.get(object, key);
                    values.put(key, value);
                }

                int matchingObjectIndex = findMatchingObjectOnArray(values, array);
                if (pathOption.isDeepMerge() && matchingObjectIndex >= 0)
                {
                    Object matchingObject = jsonProvider.get(array, matchingObjectIndex);
                    JsonPartMerger merger = new JsonPartMerger(jsonProvider,
                            absolutePath.appendIndex(matchingObjectIndex), options);
                    // The object already in the array is the higher-precedence one on the merge
                    Object merged = merger.merge(matchingObject, object);
                    // We must replace the matching entry with the merged object
                    jsonProvider.set(array, matchingObjectIndex, merged);
                }
                else if (matchingObjectIndex < 0)
                {
                    // Deep-merge not specified, so just add the object if not already in the array
                    jsonProvider.add(array, object);
                }
                // Do nothing if the key is already present.
                // It was populated from the highest-precedence json.
            }
            else
            {
                addDistinctObjectToArray(array, object);
            }
        }

        /**
         * @param distinctValues a map of distinctive key-value pairs inside the JSON object to be
         *                       used for "equal" objects identification in the given array
         * @param array          the array to be searched
         * @return the index of the first JSON object matching the specified criteria
         */
        private int findMatchingObjectOnArray(Map<String, Object> distinctValues, Object array)
        {
            return IntStream.range(0, jsonProvider.size(array)).filter(index ->
            {
                Object element = jsonProvider.get(array, index);
                return jsonProvider.isJsonObject(element)
                        && jsonObjectContains(distinctValues, element);
            }).findFirst().orElse(-1);
        }

        /**
         * @param distinctValues a map of expected keys and values to be found in the JSON
         * @param json           the JSON object to be evaluated
         * @return {@code true} if the all of the distinct key-value pairs specified are found in
         *         the given JSON object; otherwise, {@code false}
         */
        private boolean jsonObjectContains(Map<String, Object> distinctValues, Object json)
        {
            for (Entry<String, Object> entry : distinctValues.entrySet())
            {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value == null)
                {
                    LOGGER.warn("No value found for key '{}' during merge of {}", key,
                            absolutePath);
                    return false;
                }
                if (!value.equals(jsonProvider.get(json, key)))
                {
                    return false;
                }
            }
            return true;
        }

    }

}

