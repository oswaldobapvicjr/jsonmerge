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

import static java.util.stream.Collectors.toList;
import static net.obvj.jsonmerge.util.StringUtils.requireNonBlankAndTrim;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.jayway.jsonpath.InvalidPathException;

import net.obvj.jsonmerge.util.JsonPathExpression;

/**
 * An object that configures how to merge JSON objects.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 * @see GenericJsonConfigurationMerger
 */
public class JsonMergeOption
{
    private Optional<Pair<JsonPathExpression, List<String>>> distinctKeys = Optional.empty();

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
     * key inside that array, then only the object from the highest-precedence object will be
     * selected.
     * <p>
     * A {@code JsonPath} expression can be specified using either dot- or bracket-notation,
     * but complex expressions containing filters, script, subscript, or union operations, are
     * not supported.
     *
     * @param jsonPath a {@code JsonPath} expression that identifies the array; not empty
     * @param key      the key to be considered unique for objects inside an array
     *
     * @return a new {@link JsonMergeOption} with the specified pair of distinct path and key
     *
     * @throws IllegalArgumentException if one of the parameters is null or blank
     * @throws InvalidPathException     if the specified JsonPath expression is invalid
     */
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
     * keys inside that array, then only the object from the highest-precedence object will be
     * selected.
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
     */
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
     */
    private static JsonMergeOption distinctKeys(JsonPathExpression path, String... keys)
    {
        List<String> trimmedKeys = Arrays.stream(keys)
                .map(key -> requireNonBlankAndTrim(key, "The key must not be null or blank"))
                .collect(toList());

        JsonMergeOption option = new JsonMergeOption();
        option.distinctKeys = Optional.of(Pair.of(path, trimmedKeys));
        return option;
    }

    /**
     * @return a pair of JsonPath and associated distinct keys for merging a JSON array.
     */
    public Optional<Pair<JsonPathExpression, List<String>>> getDistinctKeys()
    {
        return distinctKeys;
    }

}
