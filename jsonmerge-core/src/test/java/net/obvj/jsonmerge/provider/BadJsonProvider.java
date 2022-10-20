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
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A bad {@link JsonProvider} just for testing purposes.
 *
 * @author oswaldo.bapvic.jr
 */
public class BadJsonProvider implements JsonProvider<Object>
{
    public BadJsonProvider()
    {
        throw new UnsupportedOperationException("Should not be able to instantiate me");
    }

    @Override
    public Object parse(String string)
    {
        return null;
    }

    @Override
    public Object parse(InputStream inputStream)
    {
        return null;
    }

    @Override
    public boolean isJsonObject(Object object)
    {
        return false;
    }

    @Override
    public boolean isJsonArray(Object object)
    {
        return false;
    }

    @Override
    public boolean isEmpty(Object jsonObject)
    {
        return false;
    }

    @Override
    public Object newJsonObject()
    {
        return null;
    }

    @Override
    public Object newJsonObject(Object sourceJsonObject)
    {
        return null;
    }

    @Override
    public Object newJsonArray()
    {
        return null;
    }

    @Override
    public Object newJsonArray(Object sourceJsonArray)
    {
        return null;
    }

    @Override
    public Set<Entry<String, Object>> entrySet(Object jsonObject)
    {
        return null;
    }

    @Override
    public Object get(Object jsonObject, String key)
    {
        return null;
    }

    @Override
    public Object get(Object jsonArray, int index)
    {
        return null;
    }

    @Override
    public void put(Object jsonObject, String key, Object value)
    {
        // Empty on purpose
    }

    @Override
    public void putIfAbsent(Object jsonObject, String key, Object value)
    {
        // Empty on purpose
    }

    @Override
    public void add(Object jsonArray, Object element)
    {
        // Empty on purpose
    }

    @Override
    public void set(Object jsonArray, int index, Object element)
    {
        // Empty on purpose
    }

    @Override
    public int indexOf(Object jsonArray, Object element)
    {
        return 0;
    }

    @Override
    public void forEachElementInArray(Object jsonArray, Consumer<? super Object> action)
    {
        // Empty on purpose
    }

    @Override
    public boolean arrayContains(Object jsonArray, Object element)
    {
        return false;
    }

    @Override
    public Stream<Object> stream(Object jsonArray)
    {
        return null;
    }

    @Override
    public int size(Object jsonArray)
    {
        return 0;
    }

}
