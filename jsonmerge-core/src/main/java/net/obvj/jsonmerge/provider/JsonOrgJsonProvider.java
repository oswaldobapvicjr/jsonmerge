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

import java.util.Map.Entry;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A specialized {@link JsonProvider} implementation for {@code json.org}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 *
 * @see JSONObject
 * @see JSONArray
 */
public class JsonOrgJsonProvider implements JsonProvider
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
    public Object newJsonObject()
    {
        return new JSONObject();
    }

    @Override
    public Object newJsonObject(final Object sourceJsonObject)
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

}
