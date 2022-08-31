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

package net.obvj.jsonmerge.provider;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.obvj.jsonmerge.util.JsonParseException;

/**
 * An abstraction that represents a JSON provider (for example: Jackson, Gson, etc.)
 * defining common operations for all implementations.
 *
 * @param <T> the type that represents an actual JSON object at the {@code JsonProvider}
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 */
public interface JsonProvider<T>
{

    /**
     * Deserializes a JSON string into a JSON object.
     *
     * @param string the source string
     * @return a JSON object of the type defined by this provider
     * @throws NullPointerException if the specified string is null
     * @throws JsonParseException   in the specified string is an invalid JSON
     * @since 1.1.0
     */
    T parse(String string);

    /**
     * Deserializes a JSON from a specified {@code InputStream}.
     *
     * @param inputStream the source input stream
     * @return a JSON object of the type defined by this provider
     * @throws JsonParseException in case of invalid JSON or any other exception raised by the
     *                            actual provider during parsing of the input stream
     * @since 1.1.0
     */
    T parse(InputStream inputStream);

    /**
     * Checks if the specified object is a JSON object for this provider.
     *
     * @param object the object to be checked
     * @return {@code true} if the specified object is a provider-specific JSON object;
     *         otherwise, {@code false}.
     */
    boolean isJsonObject(Object object);

    /**
     * Checks if the specified object is a JSON array for this provider.
     *
     * @param object the object to be checked
     * @return {@code true} if the specified object is a provider-specific JSON array;
     *         otherwise, {@code false}.
     */
    boolean isJsonArray(final Object object);

    /**
     * Checks if the specified JSON object contains no data.
     *
     * @param jsonObject the JSON object to be checked; not {@code null}
     * @return {@code true} if the specified JSON object contains no data; otherwise,
     *         {@code false}.
     *
     * @throws ClassCastException if the specified parameter is not a valid JSON object for
     *                            this provider
     */
    boolean isEmpty(final Object jsonObject);

    /**
     * Creates a provider-specific JSON object.
     *
     * @return a new, empty JSON object
     */
    Object newJsonObject();

    /**
     * Creates a new provider-specific JSON object with the contents of a preset JSON object.
     * <p>
     * <strong>Note:</strong> The resulting object is supposed to be <b>shallow copy</b> of
     * the source JSON object.
     *
     * @param sourceJsonObject the JSON whose contents are to be copied; not {@code null}
     * @return a new JSON object with the contents of the source JSON object
     */
    Object newJsonObject(final Object sourceJsonObject);

    /**
     * Creates a provider-specific JSON array.
     *
     * @return a new, empty JSON array
     */
    Object newJsonArray();

    /**
     * Creates a new provider-specific JSON array with the elements of a preset JSON array.
     * <p>
     * <strong>Note:</strong> Although the actual implementation may vary depending on the
     * concrete provider, the resulting object is supposed to be <b>shallow copy</b> of the
     * source JSON array.
     *
     * @param sourceJsonArray the JSON array whose contents are to be copied; not {@code null}
     * @return a new JSON array with the element of the source JSON array
     */
    Object newJsonArray(final Object sourceJsonArray);

    /**
     * Returns a {@link Set} view of the mappings contained in the specified JSON object.
     *
     * @param jsonObject the JSON object whose entries shall be accessed; not {@code null}
     * @return the entries of the specified JSON object.
     *
     * @throws ClassCastException if the specified parameter is not a valid JSON object for
     *                            this provider
     */
    Set<Map.Entry<String, Object>> entrySet(final Object jsonObject);

    /**
     * Returns the value to which the specified key is mapped in the specified JSON object; or
     * {@code null} if the JSON does not contain the specified key.
     *
     * @param jsonObject the JSON object; not {@code null}
     * @param key        the key to be searched; not {@code null}
     * @return the value associated with the specified key in the specified JSON
     *
     * @throws ClassCastException if the specified {@code jsonObject} is not a valid JSON
     *                            object for this provider
     */
    Object get(final Object jsonObject, final String key);

    /**
     * Returns the element at the specified position in the specified JSON array.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @param index     index of the element to return
     * @return the element at the specified position in the specified JSON array
     * @throws ClassCastException        if the specified {@code jsonObject} is not a valid
     *                                   JSON object for this provider
     * @throws IndexOutOfBoundsException if the index is out of range (either negative or
     *                                   greater then the size of the array)
     * @since 1.1.0
     */
    Object get(final Object jsonArray, int index);

    /**
     * Associates the specified value with the specified key in the specified JSON object.
     *
     * @param jsonObject the JSON object; not {@code null}
     * @param key        the key with which the specified value is to be associated in the
     *                   JSON; not {@code null}
     * @param value      the value to be associated with the specified key in the JSON
     *
     * @throws ClassCastException if the specified {@code jsonObject} is not a valid JSON
     *                            object for this provider
     */
    void put(final Object jsonObject, final String key, final Object value);

    /**
     * Associates the specified value with the specified key in the specified JSON object,
     * provided that the specified key is not already associated with a value in the JSON.
     *
     * @param jsonObject the JSON object; not {@code null}
     * @param key        the key with which the specified value is to be associated in the
     *                   JSON; not {@code null}
     * @param value      the value to be associated with the specified key in the JSON,
     *                   provided that the specified key is absent in the document
     *
     * @throws ClassCastException if the specified {@code jsonObject} is not a valid JSON
     *                            object for this provider
     */
    void putIfAbsent(final Object jsonObject, final String key, final Object value);

    /**
     * Appends the specified element to the end of the specified JSON array.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @param element   the element to be added
     *
     * @throws ClassCastException if the specified {@code jsonArray} is not a valid JSON array
     *                            for this provider
     */
    void add(final Object jsonArray, final Object element);

    /**
     * Replaces the element at the specified position in the JSON array with the specified
     * element.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @param index     the index of the element to replace
     * @param element   the element to be stored at the specified position
     *
     * @throws ClassCastException        if the specified {@code jsonArray} is not a valid
     *                                   JSON array for this provider
     * @throws IndexOutOfBoundsException if the index is out of range (either negative or
     *                                   greater then the size of the array)
     * @since 1.1.0
     */
    void set(final Object jsonArray, int index, final Object element);

    /**
     * Returns the index of the first occurrence of the specified element in the specified
     * JSON array, or -1 if there is no such index.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @param element   the element to search for
     * @return the index of the first occurrence of the specified element in the array, or -1
     *         if the array does not contain the element
     *
     * @throws ClassCastException if the specified {@code jsonArray} is not a valid JSON array
     *                            for this provider
     * @since 1.1.0
     */
    int indexOf(final Object jsonArray, final Object element);

    /**
     * Performs the given action for each element of the specified JSON array until all
     * entries have been processed.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @param action    the action to be performed for each element; not {@code null}
     *
     * @throws ClassCastException   if the specified {@code jsonArray} is not a valid JSON
     *                              array for this provider
     * @throws NullPointerException if the specified action is {@code null}
     */
    void forEachElementInArray(final Object jsonArray, final Consumer<? super Object> action);

    /**
     * Checks if the specified JSON array contains the specified element.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @param element   the element to be searched
     * @return {@code true} if the specified JSON array contains the specified element;
     *         otherwise, {@code false}.
     *
     * @throws ClassCastException if the specified {@code jsonArray} is not a valid JSON array
     *                            for this provider
     */
    boolean arrayContains(final Object jsonArray, final Object element);

    /**
     * Returns a sequential {@code Stream} with the specified JSON array as its source.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @return a sequential {@code Stream} over the elements in this JSON array
     *
     * @throws ClassCastException if the specified {@code jsonArray} is not a valid JSON array
     *                            for this provider
     */
    Stream<Object> stream(final Object jsonArray);

    /**
     * Returns the number of elements in the specified JSON array.
     *
     * @param jsonArray the JSON array; not {@code null}
     * @return the number of elements in the specified JSON array
     * @since 1.1.0
     */
    int size(final Object jsonArray);

}
