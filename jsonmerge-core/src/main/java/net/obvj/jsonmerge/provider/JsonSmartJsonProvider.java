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
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * A specialized {@link JsonProvider} implementation for {@code json-smart}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 *
 * @see <a href="https://github.com/netplex/json-smart-v2">json-smart Project home</a>
 * @see net.minidev.json.JSONObject
 * @see net.minidev.json.JSONArray
 */
public class JsonSmartJsonProvider implements JsonProvider<JSONObject>
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
    public JSONObject parse(InputStream inputStream)
    {
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        try
        {
            return parser.parse(inputStream, JSONObject.class);
        }
        catch (UnsupportedEncodingException | ParseException e)
        {
            // TODO Replace with more specific exception
            throw new RuntimeException(e);
        }
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
        return new JSONObject((JSONObject) sourceJsonObject);
    }

    @Override
    public Object newJsonArray()
    {
        return new JSONArray();
    }

    @Override
    public Object newJsonArray(final Object sourceJsonArray)
    {
        JSONArray array = new JSONArray();
        array.addAll((JSONArray) sourceJsonArray);
        return array;
    }

    @Override
    public Set<Entry<String, Object>> entrySet(final Object jsonObject)
    {
        return toJsonObject(jsonObject).entrySet();
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
        toJsonObject(jsonObject).put(key, value);
    }

    @Override
    public void putIfAbsent(final Object jsonObject, final String key, final Object value)
    {
        toJsonObject(jsonObject).putIfAbsent(key, value);
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
        return toJsonArray(jsonArray).indexOf(element);
    }

    @Override
    public void forEachElementInArray(final Object jsonArray, final Consumer<? super Object> action)
    {
        toJsonArray(jsonArray).forEach(action);
    }

    @Override
    public boolean arrayContains(final Object jsonArray, final Object element)
    {
        return toJsonArray(jsonArray).contains(element);
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
