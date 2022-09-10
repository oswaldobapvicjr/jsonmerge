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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import net.obvj.jsonmerge.provider.GsonJsonProvider;
import net.obvj.jsonmerge.provider.JsonProvider;

/**
 * Unit tests for the {@link JsonMerger} using the {@link GsonJsonProvider}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class JsonMergerGsonJsonProviderTest extends JsonMergerTest<JsonObject>
{

    private static Configuration configuration = Configuration.builder()
            .jsonProvider(new com.jayway.jsonpath.spi.json.GsonJsonProvider())
            .mappingProvider(new GsonMappingProvider()).build();

    private static ParseContext context = JsonPath.using(configuration);

    private final Gson gson = new Gson();

    @Override
    JsonProvider<JsonObject> getProvider()
    {
        return new GsonJsonProvider();
    }

    @Override
    Object get(JsonObject object, String jsonPath)
    {
        return context.parse(object).read(jsonPath);
    }

    @Override
    void assertElement(Object expected, Object actual)
    {
        assertEquals(gson.toJsonTree(expected), actual);
    }

    @Override
    void assertArray(List<?> expected, JsonObject result, String jsonPath)
    {
        assertArray(expected, result, jsonPath, true);
    }

    @Override
    void assertArray(List<?> expected, JsonObject result, String jsonPath, boolean exactSize)
    {
        JsonArray array = (JsonArray) get(result, jsonPath);
        assertArray(expected, array, exactSize);
    }

    private void assertArray(List<?> expected, JsonArray array, boolean exactSize)
    {
        expected.forEach(element -> assertTrue(array.contains(gson.toJsonTree(element)),
                () -> String.format("Expected element %s not found in array %s", element, array)));

        if (exactSize)
        {
            assertEquals(expected.size(), array.size());
        }
    }

    @Override
    Class<JsonObject> getObjectType()
    {
        return JsonObject.class;
    }

}
