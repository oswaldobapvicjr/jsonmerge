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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * A specialized {@link JsonProvider} implementation for {@code Vert.x}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.2.0
 *
 * @see <a href="https://vertx.io/">Vert.x Project home</a>
 * @see io.vertx.core.json.JsonObject
 * @see io.vertx.core.json.JsonArray
 */
public class VertxJsonProvider extends AbstractJsonProvider<JsonObject>
{

    private static final int BUFFER_SIZE = 4096;

    private JsonObject toJsonObject(final Object jsonObject)
    {
        return (JsonObject) jsonObject;
    }

    private JsonArray toJsonArray(final Object jsonArray)
    {
        return (JsonArray) jsonArray;
    }

    @Override
    public JsonObject parse(String string)
    {
        return new JsonObject(string);
    }

    @Override
    JsonObject doParse(InputStream inputStream) throws IOException
    {
        return new JsonObject(toBuffer(inputStream));
    }

    private static Buffer toBuffer(InputStream inputStream) throws IOException
    {
        Buffer buffer = Buffer.buffer();
        if (inputStream != null)
        {
            int read;
            byte[] data = new byte[BUFFER_SIZE];
            while ((read = inputStream.read(data, 0, data.length)) != -1)
            {
                if (read == data.length)
                {
                    buffer.appendBytes(data);
                }
                else
                {
                    byte[] slice = new byte[read];
                    System.arraycopy(data, 0, slice, 0, slice.length);
                    buffer.appendBytes(slice);
                }
            }
        }
        return buffer;
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
        return toJsonObject(jsonObject).isEmpty();
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
        toJsonObject(sourceJsonObject).forEach(entry -> json.put(entry.getKey(), entry.getValue()));
        return json;
    }

    @Override
    public Object newJsonArray()
    {
        return new JsonArray();
    }

    @Override
    public Object newJsonArray(final Object sourcejsonArray)
    {
        JsonArray array = new JsonArray();
        array.addAll((JsonArray) sourcejsonArray);
        return array;
    }

    @Override
    public Set<Entry<String, Object>> entrySet(final Object jsonObject)
    {
        Spliterator<Entry<String, Object>> spliterator = toJsonObject(jsonObject).spliterator();
        return StreamSupport.stream(spliterator, false).collect(Collectors.toSet());
    }

    @Override
    public Object get(final Object jsonObject, final String key)
    {
        return toJsonObject(jsonObject).getValue(key);
    }

    @Override
    public Object get(final Object jsonArray, int index)
    {
        return toJsonArray(jsonArray).getValue(index);
    }

    @Override
    public void put(final Object jsonObject, final String key, final Object value)
    {
        toJsonObject(jsonObject).put(key, value);
    }

    @Override
    public void putIfAbsent(final Object jsonObject, final String key, final Object value)
    {
        JsonObject json = toJsonObject(jsonObject);
        if (json.getValue(key) == null)
        {
            json.put(key, value);
        }
    }

    @Override
    public void add(final Object jsonArray, final Object element)
    {
        toJsonArray(jsonArray).add(element);
    }

    @Override
    public void set(Object jsonArray, int index, Object element)
    {
        toJsonArray(jsonArray).set(index, element);
    }

    @Override
    public int indexOf(Object jsonArray, Object element)
    {
        JsonArray array = toJsonArray(jsonArray);
        return IntStream.range(0, array.size())
                .filter(index -> Objects.equals(array.getValue(index), element))
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
        Object searcheable = isJsonObject(element) ? toJsonObject(element).getMap() : element;
        return toJsonArray(jsonArray).getList().contains(searcheable);
    }

    @Override
    public Stream<Object> stream(final Object jsonArray)
    {
        return toJsonArray(jsonArray).stream();
    }

    @Override
    public int size(Object jsonArray)
    {
        return toJsonArray(jsonArray).size();
    }

}
