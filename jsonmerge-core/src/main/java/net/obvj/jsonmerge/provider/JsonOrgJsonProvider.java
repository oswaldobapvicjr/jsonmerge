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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A specialized {@link JsonProvider} implementation for {@code json.org}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 *
 * @see <a href="https://github.com/stleary/JSON-java">JSON-java Project home</a>
 * @see org.json.JSONObject
 * @see org.json.JSONArray
 */
public class JsonOrgJsonProvider extends AbstractJsonProvider<JSONObject>
{

    private JSONObject toJsonObject(final Object jsonObject)
    {
        return (JSONObject) jsonObject;
    }

    private JSONArray toJsonArray(final Object jsonArray)
    {
        return (JSONArray) jsonArray;
    }

    @Override
    JSONObject doParse(InputStream inputStream)
    {
        JSONTokener tokener = new JSONTokener(inputStream);
        return new JSONObject(tokener);
    }

    @Override
    public boolean isJsonObject(final Object object)
    {
        return object instanceof JSONObject;
    }

    @Override
    public boolean isJsonArray(final Object object)
    {
        return object instanceof JSONArray;
    }

    @Override
    public boolean isEmpty(final Object jsonObject)
    {
        return toJsonObject(jsonObject).isEmpty();
    }

    @Override
    public JSONObject newJsonObject()
    {
        return new JSONObject();
    }

    @Override
    public JSONObject newJsonObject(final Object sourceJsonObject)
    {
        JSONObject source = toJsonObject(sourceJsonObject);
        JSONObject target = new JSONObject();
        source.keySet().forEach((String key) ->
        {
            Object value = source.opt(key);
            target.put(key, value);
        });
        return target;
    }

    @Override
    public Object newJsonArray()
    {
        return new JSONArray();
    }

    @Override
    public Object newJsonArray(final Object sourceJsonArray)
    {
        return new JSONArray(toJsonArray(sourceJsonArray));
    }

    @Override
    public Set<Entry<String, Object>> entrySet(final Object jsonObject)
    {
        JSONObject json = toJsonObject(jsonObject);
        return json.keySet().stream().collect(Collectors.toMap(Function.identity(), json::opt)).entrySet();
    }

    @Override
    public Object get(final Object jsonObject, final String key)
    {
        return toJsonObject(jsonObject).opt(key);
    }

    @Override
    public Object get(final Object jsonArray, int index)
    {
        return toJsonArray(jsonArray).get(index);
    }

    @Override
    public void put(final Object jsonObject, final String key, final Object value)
    {
        toJsonObject(jsonObject).put(key, value);
    }

    @Override
    public void putIfAbsent(final Object jsonObject, final String key, final Object value)
    {
        JSONObject json = toJsonObject(jsonObject);
        if (json.opt(key) == null)
        {
            json.put(key, value);
        }
    }

    @Override
    public void add(final Object jsonArray, final Object element)
    {
        toJsonArray(jsonArray).put(element);
    }

    @Override
    public void set(Object jsonArray, int index, Object element)
    {
        toJsonArray(jsonArray).put(index, element);
    }

    @Override
    public int indexOf(Object jsonArray, Object element)
    {
        JSONArray array = toJsonArray(jsonArray);
        return IntStream.range(0, array.length())
                .filter(index -> Objects.equals(array.get(index), element))
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
        return stream(jsonArray).anyMatch(arrayElement -> similarObjects(arrayElement, element));
    }

    private boolean similarObjects(Object first, Object second)
    {
        return first.equals(second)
                || (first instanceof JSONObject && ((JSONObject) first).similar(second))
                || (first instanceof JSONArray && ((JSONArray) first).similar(second));
    }

    @Override
    public Stream<Object> stream(final Object jsonArray)
    {
        Spliterator<Object> spliterator = toJsonArray(jsonArray).spliterator();
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public int size(Object jsonArray)
    {
        return toJsonArray(jsonArray).length();
    }

}
