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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static net.obvj.jsonmerge.util.StringUtils.requireNonBlankAndTrim;

import java.util.Arrays;
import java.util.List;

import com.jayway.jsonpath.InvalidPathException;

import net.obvj.jsonmerge.util.JsonPathExpression;

/**
 * An object that contains parameters about how to merge a specific path of a JSON
 * document.
 * <p>
 * Use {@link JsonMergeOption#onPath(String)} to initialize the construction of a
 * {@code JsonMergeOption} with a specific document path, then let the API guide you
 * through the additional builder methods to finalize the construction of the desired
 * merge option.
 * <p>
 * Examples:
 *
 * <blockquote>
 *
 * <pre>
 * {@code JsonMergeOption.onPath("$.parameters")}
 * {@code         .findObjectsIdentifiedBy("name")}
 * {@code         .thenPickTheHigherPrecedenceOne();}
 *
 * {@code JsonMergeOption.onPath("$.files")}
 * {@code         .findObjectsIdentifiedBy("id", "version")}
 * {@code         .thenDoADeepMerge();}
 *
 * {@code JsonMergeOption.onPath("$.files")}
 * {@code         .addDistinctObjectsOnly();}
 *
 * {@code JsonMergeOption.onPath("$.files")}
 * {@code         .addAll();}
 * </pre>
 *
 * </blockquote>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
public final class JsonMergeOption
{
    private static final String TO_STRING_FORMAT = "JsonMergeOption (path=%s, keys=%s, deepMerge=%s, distinctObjectsOnly=%s)";

    private static final boolean DEFAULT_DEEP_MERGE = false;
    private static final boolean DEFAULT_DISTINCT_OBJECTS_ONLY = true;

    /**
     * The default merge option.
     *
     * @since 1.1.0
     */
    public static final JsonMergeOption DEFAULT = new JsonMergeOption(new JsonPathExpression("@"),
            emptyList(), DEFAULT_DEEP_MERGE, DEFAULT_DISTINCT_OBJECTS_ONLY);

    private final JsonPathExpression path;
    private final List<String> keys;
    private final boolean deepMerge;
    private final boolean distinctObjectsOnly;

    private JsonMergeOption(JsonPathExpression path, List<String> keys, boolean deepMerge,
            boolean distinctObjectsOnly)
    {
        this.path = path;
        this.keys = unmodifiableList(keys);
        this.deepMerge = deepMerge;
        this.distinctObjectsOnly = distinctObjectsOnly;
    }

    /**
     * Specifies a distinct key for objects inside an array identified by a given
     * {@code JsonPath} expression.
     * <p>
     * For example, consider the following document: <blockquote>
     *
     * <pre>
     * {
     *   "params": [
     *     {
     *       "param": "name",
     *       "value": "John Doe"
     *     },
     *     {
     *       "param": "age",
     *       "value": 33
     *     }
     *   ]
     * }
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * A {@code MergeOption} of key {@code "param"} associated with the {@code JsonPath}
     * expression {@code "$.params"} tells the algorithm to check for distinct objects
     * identified by the same value inside the {@code "param"} field during the merge of the
     * {@code "$.params"} array.
     * <p>
     * In other words, if two JSON documents contain different objects identified by the same
     * key inside that array, then only the object from the highest-precedence JSON document
     * will be selected.
     * <p>
     * A {@code JsonPath} expression can be specified using either dot- or bracket-notation,
     * but complex expressions containing filters, script, subscript, or union operations, are
     * not supported.
     *
     * @param jsonPath a {@code JsonPath} expression that identifies the array; not empty
     * @param key      the key to be considered unique for objects inside an array
     *
     * @return a new {@code JsonMergeOption} with the specified pair of distinct path and key
     *
     * @throws IllegalArgumentException if one of the parameters is null or blank
     * @throws InvalidPathException     if the specified JsonPath expression is invalid
     * @deprecated Use {@link JsonMergeOption#onPath(String)} instead
     */
    @Deprecated
    public static JsonMergeOption distinctKey(String jsonPath, String key)
    {
        return distinctKeys(jsonPath, key);
    }

    /**
     * Specifies one or more distinct keys for objects inside an array identified by a given
     * {@code JsonPath} expression.
     * <p>
     * For example, consider the following document: <blockquote>
     *
     * <pre>
     * {
     *   "files": [
     *     {
     *       "id": "d2b638be-40d2-4965-906e-291521f8a19d",
     *       "version": "1",
     *       "date": "2022-07-07T10:42:21"
     *     },
     *     {
     *       "id": "d2b638be-40d2-4965-906e-291521f8a19d",
     *       "version": "2",
     *       "date": "2022-08-06T09:40:01"
     *     },
     *     {
     *       "id": "9570cc646-1586-11ed-861d-0242ac120002",
     *       "version": "1",
     *       "date": "2022-08-06T09:51:40"
     *     }
     *   ]
     * }
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * A {@code MergeOption} of keys {@code "id"} and {@code "version"} associated with the
     * {@code JsonPath} expression {@code "$.files"} tells the algorithm to check for distinct
     * objects identified by the same values in both fields {@code "id"} and {@code "version"}
     * during the merge of the {@code "$.files"} array.
     * <p>
     * In other words, if two JSON documents contain different objects identified by the same
     * keys inside that array, then only the object from the highest-precedence JSON document
     * will be selected.
     * <p>
     * A {@code JsonPath} expression can be specified using either dot- or bracket-notation,
     * but complex expressions containing filters, script, subscript, or union operations, are
     * not supported.
     *
     * @param jsonPath a {@code JsonPath} expression that identifies the array; not empty
     * @param keys     one or more keys to be considered unique for objects inside an array
     *
     * @return a new {@link JsonMergeOption} with the specified pair of path and distinct keys
     *
     * @throws IllegalArgumentException if one of the parameters is null or blank
     * @throws InvalidPathException     if the specified JsonPath expression is invalid
     * @deprecated Use {@link JsonMergeOption#onPath(String)} instead
     */
    @Deprecated
    public static JsonMergeOption distinctKeys(String jsonPath, String... keys)
    {
        return distinctKeys(new JsonPathExpression(jsonPath), keys);
    }

    /**
     * Specifies one or more distinct keys for objects inside an array identified by a given
     * {@code JsonPathExpression}.
     *
     * @param path a {@link JsonPathExpression} that identifies the array; not empty
     * @param keys one or more keys to be considered unique for objects inside an array
     *
     * @return a new {@link JsonMergeOption} with the specified pair of distinct key and path
     *
     * @throws IllegalArgumentException if one of the specified parameters is null or blank
     * @throws InvalidPathException     if the specified JsonPath expression is invalid
     * @deprecated Use {@link JsonMergeOption#onPath(String)} instead
     */
    @Deprecated
    private static JsonMergeOption distinctKeys(JsonPathExpression path, String... keys)
    {
        List<String> trimmedKeys = Arrays.stream(keys)
                .map(key -> requireNonBlankAndTrim(key, "The key must not be null or blank"))
                .collect(toList());

        return new JsonMergeOption(path, trimmedKeys, DEFAULT_DEEP_MERGE,
                DEFAULT_DISTINCT_OBJECTS_ONLY);
    }

    /**
     * Initializes the construction of a {@code JsonMergeOption} for the document path
     * determined by the given {@code JsonPath} expression.
     * <p>
     * A {@code JsonPath} expression can be specified using either dot- or bracket-notation,
     * but complex expressions containing filters, script, subscript, or union operations, are
     * not fully supported.
     *
     * <h4>Examples of valid expressions:</h4>
     * <h5>Using dot-notation:</h5>
     * <ul>
     * <li>{@code $.sites}</li>
     * <li>{@code $.sites.*.users}</li>
     * <li>{@code $.sites.*.users.*.preferences}</li>
     * </ul>
     * <h5>Using bracket-notation:</h5>
     * <ul>
     * <li>{@code $['sites']}</li>
     * <li>{@code $['sites'][*]['users']}</li>
     * <li>{@code $['sites'][*]['users'][*]['preferences']}</li>
     * </ul>
     * <h5>Sample JSON:</h5>
     *
     * <pre>
     * {
     *   "sites": [
     *     {
     *       "id": "europe-1",
     *       "users": [
     *         {
     *           "email": "camillelawson@zolarex.com",
     *           "preferences": [
     *             {
     *               "key": "theme",
     *               "value": "light"
     *             }
     *           ]
     *         }
     *       ]
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param jsonPath a {@code JsonPath} expression that identifies the document part that
     *                 should receive special handling during the merge; not empty
     * @return a {@code JsonMergeOption} builder initialized with the specified
     *         {@code JsonPath}
     *
     * @throws IllegalArgumentException if the specified expression is null or empty
     * @throws InvalidPathException     if the specified {@code JsonPath} expression is
     *                                  invalid
     * @since 1.1.0
     */
    public static Builder onPath(String jsonPath)
    {
        JsonPathExpression compiledJsonPath = new JsonPathExpression(jsonPath);
        return new Builder(compiledJsonPath);
    }

    /**
     * @return a expression that represents a specific path of the JSON document that should
     *         receive special handling during the merge; not empty
     * @since 1.1.0
     */
    public JsonPathExpression getPath()
    {
        return path;
    }

    /**
     * @return a list of keys to be considered for distinct JSON objects identification inside
     *         the array represented by {@link #getPath()}; not null
     * @since 1.1.0
     */
    public List<String> getKeys()
    {
        return keys;
    }

    /**
     * @return a flag indicating whether or not to do a deep merge of the elements inside the
     *         document path represented by {@link #getPath()}.
     * @since 1.1.0
     */
    public boolean isDeepMerge()
    {
        return deepMerge;
    }

    /**
     * @return a flag to determine that duplicate objects should not be added during the merge
     *         of the document path represented by {@link #getPath()}.
     * @since 1.1.0
     */
    public boolean isDistinctObjectsOnly()
    {
        return distinctObjectsOnly;
    }

    /**
     * Returns a string representation of this {@code JsonMergeOption}.
     *
     * @return a string representation of this object
     * @since 1.1.0
     */
    @Override
    public String toString()
    {
        return String.format(TO_STRING_FORMAT, path, keys, deepMerge, distinctObjectsOnly);
    }

    /**
     * A builder for {@link JsonMergeOption}, with the document path already set.
     *
     * @author oswaldo.bapvic.jr
     * @since 1.1.0
     */
    public static class Builder
    {
        private final JsonPathExpression path;

        private Builder(JsonPathExpression path)
        {
            this.path = path;
        }

        /**
         * Defines one or more keys to determine object equality inside the specified document
         * path, provided that the path is an array path.
         * <p>
         * For example, consider the following JSON document: <blockquote>
         *
         * <pre>
         * {
         *   "params": [
         *     {
         *       "name": "country",
         *       "value": "Brazil"
         *     },
         *     {
         *       "name": "language",
         *       "value": "pt-BR"
         *     }
         *   ]
         * }
         * </pre>
         *
         * </blockquote>
         * <p>
         * A {@code JsonMergeOption} associating the {@code "name"} key with the
         * {@code "$.params"} path tells the algorithm to find distinct objects identified by the
         * {@code "name"} key during the merge of the {@code "$.params"} array.
         * <p>
         * In other words, if two JSON documents contain different objects with same value for the
         * provided key(s), then they will be considered as "equal" during the merge.
         *
         * @param keys one or more keys to determine object equality inside an array path
         * @return an intermediary {@code JsonMergeOption} build stage with the specified key(s)
         * @throws IllegalArgumentException if a {@code null} or blank key is received
         */
        public BuilderWithKeys findObjectsIdentifiedBy(String... keys)
        {
            List<String> trimmedKeys = Arrays.stream(keys)
                    .map(key -> requireNonBlankAndTrim(key, "The key must not be null or blank"))
                    .collect(toList());
            return new BuilderWithKeys(path, trimmedKeys);
        }

        /**
         * Avoid duplicate objects during the merge of the specified array path (default option).
         *
         * @return a finalized {@link JsonMergeOption}
         */
        public JsonMergeOption addDistinctObjectsOnly()
        {
            return new JsonMergeOption(path, emptyList(), DEFAULT_DEEP_MERGE, true);
        }

        /**
         * Add all elements of the both JSON documents during the merge of the specified array
         * path, with no duplication check.
         *
         * @return a finalized {@link JsonMergeOption}
         */
        public JsonMergeOption addAll()
        {
            return new JsonMergeOption(path, emptyList(), DEFAULT_DEEP_MERGE, false);
        }
    }

    /**
     * An intermediary {@link JsonMergeOption} build stage that is applicable when one or more
     * distinct keys are specified for a given document path.
     *
     * @author oswaldo.bapvic.jr
     * @since 1.1.0
     */
    public static class BuilderWithKeys
    {
        private final JsonPathExpression path;
        private final List<String> keys;

        private BuilderWithKeys(JsonPathExpression path, List<String> keys)
        {
            this.path = path;
            this.keys = keys;
        }

        /**
         * Tells the algorithm to pick the higher precedence object when two objects identified by
         * the same key(s) are found in both JSON documents at the path defined for the
         * {@code JsonMergeOption}.
         *
         * @return a finalized {@link JsonMergeOption}
         */
        public JsonMergeOption thenPickTheHigherPrecedenceOne()
        {
            return new JsonMergeOption(path, keys, false, DEFAULT_DISTINCT_OBJECTS_ONLY);
        }

        /**
         * Tells the algorithm to do a deep merge if two objects identified by the same key(s) are
         * found in both JSON documents at the path defined for the {@code JsonMergeOption}.
         *
         * @return a finalized {@link JsonMergeOption}
         */
        public JsonMergeOption thenDoADeepMerge()
        {
            return new JsonMergeOption(path, keys, true, DEFAULT_DISTINCT_OBJECTS_ONLY);
        }
    }

}
