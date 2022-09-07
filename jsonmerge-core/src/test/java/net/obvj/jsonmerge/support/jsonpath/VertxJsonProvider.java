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

package net.obvj.jsonmerge.support.jsonpath;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.jayway.jsonpath.spi.json.AbstractJsonProvider;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.obvj.jsonmerge.provider.JsonProvider;

/**
 * A {@link com.jayway.jsonpath.spi.json.JsonProvider} for Vert.x JsonObject.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
public class VertxJsonProvider extends AbstractJsonProvider
{
    private JsonProvider<JsonObject> internal = new net.obvj.jsonmerge.provider.VertxJsonProvider();

    @Override
    public Object parse(String json)
    {
        return internal.parse(json);
    }

    @Override
    public Object parse(InputStream jsonStream, String charset)
    {
        return internal.parse(jsonStream);
    }

    @Override
    public String toJson(Object obj)
    {
        return toJsonObject(obj).encodePrettily();
    }

    @Override
    public Object createArray()
    {
        return internal.newJsonArray();
    }

    @Override
    public Object createMap()
    {
        return internal.newJsonObject();
    }

    @Override
    public boolean isArray(Object obj)
    {
        return (obj instanceof JsonArray || obj instanceof List);
    }

    @Override
    public Object getArrayIndex(Object obj, int idx)
    {
        return internal.get(obj, idx);
    }

    @Override
    public void setArrayIndex(Object array, int index, Object newValue)
    {
        if (!isArray(array))
        {
            throw new UnsupportedOperationException();
        }
        setProperty(array, index, newValue);
    }

    @Override
    public Object getMapValue(Object obj, String key)
    {
        if (!toJsonObject(obj).containsKey(key))
        {
            return UNDEFINED;
        }
        return internal.get(obj, key);
    }

    @Override
    public void setProperty(Object obj, Object key, Object value)
    {
        if (isMap(obj))
        {
            toJsonObject(obj).put(key.toString(), value);
        }
        else
        {
            JsonArray array = toJsonArray(obj);
            int index = parseIndex(key, array);
            if (index == array.size())
            {
                array.add(value);
            }
            else
            {
                array.set(index, value);
            }
        }

    }

    private int parseIndex(Object key, JsonArray array)
    {
        if (key != null)
        {
            return key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
        }
        return array.size();
    }

    @Override
    public void removeProperty(Object obj, Object key)
    {
        if (isMap(obj))
        {
            toJsonObject(obj).remove(key.toString());
        }
        else
        {
            JsonArray array = toJsonArray(obj);
            int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            array.remove(index);
        }
    }

    @Override
    public boolean isMap(Object obj)
    {
        return internal.isJsonObject(obj);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj)
    {
        JsonObject jsonObject = toJsonObject(obj);
        return Objects.isNull(jsonObject.fieldNames()) ? new ArrayList<>()
                : jsonObject.fieldNames();
    }

    @Override
    public int length(Object obj)
    {
        if (isArray(obj))
        {
            return toJsonArray(obj).size();
        }
        if (isMap(obj))
        {
            return toJsonObject(obj).size();
        }
        return super.length(obj);
    }

    @Override
    public Iterable<?> toIterable(Object obj)
    {
        if (isArray(obj))
        {
            return toJsonArray(obj);
        }
        return toJsonObject(obj).getMap().entrySet();
    }

    private JsonArray toJsonArray(Object o)
    {
        return (JsonArray) o;
    }

    private JsonObject toJsonObject(Object o)
    {
        return (JsonObject) o;
    }

}
