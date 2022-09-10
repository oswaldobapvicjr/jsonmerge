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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link MappingProvider} for Vert.x JsonObject.
 *
 * @author oswado.bapvic.jr
 * @since 1.2.0
 */
public class VertxMappingProvider implements MappingProvider
{
    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration)
    {
        if (source == null)
        {
            return null;
        }
        if (targetType.equals(Object.class) || targetType.equals(List.class)
                || targetType.equals(Map.class))
        {
            return (T) mapToObject(source);
        }
        return (T) source;
    }

    @Override
    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration)
    {
        throw new UnsupportedOperationException("Vertx provider does not support TypeRef");
    }

    private Object mapToObject(Object source)
    {
        if (source instanceof JsonArray)
        {
            List<Object> mapped = new ArrayList<Object>();
            JsonArray array = (JsonArray) source;

            for (int i = 0; i < array.size(); i++)
            {
                mapped.add(mapToObject(array.getValue(i)));
            }

            return mapped;
        }
        else if (source instanceof JsonObject)
        {
            Map<String, Object> mapped = new HashMap<String, Object>();
            JsonObject obj = (JsonObject) source;

            for (Object o : obj.fieldNames())
            {
                String key = o.toString();
                mapped.put(key, mapToObject(obj.getValue(key)));
            }
            return mapped;
        }
        else
        {
            return source;
        }
    }
}

