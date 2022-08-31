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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A specialized {@link JsonProvider} implementation for {@code Gson}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 *
 * @see <a href="https://github.com/google/gson">Gson Project home</a>
 * @see com.google.gson.JsonObject
 * @see com.google.gson.JsonArray
 */
public class GsonJsonProvider extends AbstractJsonProvider<JsonObject>
{
    private final Gson gson = new Gson();

    private JsonObject toJsonObject(final Object jsonObject)
    {
        return (JsonObject) jsonObject;
    }

    private JsonArray toJsonArray(final Object jsonArray)
    {
        return (JsonArray) jsonArray;
    }

    JsonElement toJsonElement(final Object object)
    {
        return object instanceof JsonElement ? (JsonElement) object : gson.toJsonTree(object);
    }

    @Override
    public JsonObject doParse(InputStream inputStream)
    {
        Reader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, JsonObject.class);
    }

    @Override
    public boolean isJsonObject(final Object object)
    {
        return object instanceof JsonObject;
    }

    @Override
    public boolean isJsonArray(final Object object)
    {
        return object instanceof JsonArray;
    }

    @Override
    public boolean isEmpty(final Object jsonObject)
    {
        return toJsonObject(jsonObject).size() == 0;
    }

    @Override
    public Object newJsonObject()
    {
        return new JsonObject();
    }

    @Override
    public Object newJsonObject(final Object sourceJsonObject)
    {
        JsonObject json = new JsonObject();
        toJsonObject(sourceJsonObject).entrySet()
                .forEach(entry -> json.add(entry.getKey(), entry.getValue()));
        return json;
    }

    @Override
    public Object newJsonArray()
    {
        return new JsonArray();
    }

    @Override
    public Object newJsonArray(final Object sourceJsonArray)
    {
        JsonArray array = new JsonArray();
        array.addAll(toJsonArray(sourceJsonArray));
        return array;
    }

    @Override
    public Set<Entry<String, Object>> entrySet(final Object jsonObject)
    {
        return (Set) toJsonObject(jsonObject).entrySet();
    }

    @Override
    public Object get(final Object jsonObject, final String key)
    {
        return toJsonObject(jsonObject).get(key);
    }

    @Override
    public Object get(final Object jsonArray, int index)
    {
        return toJsonArray(jsonArray).get(index);
    }

    @Override
    public void put(final Object jsonObject, final String key, final Object value)
    {
        toJsonObject(jsonObject).add(key, toJsonElement(value));
    }

    @Override
    public void putIfAbsent(final Object jsonObject, final String key, final Object value)
    {
        JsonObject json = toJsonObject(jsonObject);
        if (json.get(key) == null)
        {
            json.add(key, toJsonElement(value));
        }
    }

    @Override
    public void add(final Object jsonArray, final Object element)
    {
        toJsonArray(jsonArray).add(toJsonElement(element));
    }

    @Override
    public void set(Object jsonArray, int index, Object element)
    {
        toJsonArray(jsonArray).set(index, toJsonElement(element));
    }

    @Override
    public int indexOf(Object jsonArray, Object element)
    {
        JsonArray array = toJsonArray(jsonArray);
        return IntStream.range(0, array.size())
                .filter(index -> Objects.equals(array.get(index), toJsonElement(element)))
                .findFirst().orElse(-1);
    }

    @Override
    public void forEachElementInArray(final Object jsonArray, final Consumer<? super Object> action)
    {
        toJsonArray(jsonArray).forEach(action);
    }

    @Override
    public boolean arrayContains(final Object jsonArray, final Object element)
    {
        return toJsonArray(jsonArray).contains(toJsonElement(element));
    }

    @Override
    public Stream<Object> stream(final Object jsonArray)
    {
        Spliterator<JsonElement> spliterator = toJsonArray(jsonArray).spliterator();
        return (Stream) StreamSupport.stream(spliterator, false);
    }

    @Override
    public int size(Object jsonArray)
    {
        return toJsonArray(jsonArray).size();
    }

}
