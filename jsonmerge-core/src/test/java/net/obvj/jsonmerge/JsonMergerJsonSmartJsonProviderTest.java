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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.obvj.jsonmerge.provider.JsonProvider;
import net.obvj.jsonmerge.provider.JsonSmartJsonProvider;

/**
 * Unit tests for the {@link JsonMerger} using the {@link JsonSmartJsonProvider}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class JsonMergerJsonSmartJsonProviderTest extends JsonMergerTest<JSONObject>
{

    private static Configuration configuration = Configuration.builder()
            .jsonProvider(new com.jayway.jsonpath.spi.json.JsonSmartJsonProvider())
            .mappingProvider(new JsonSmartMappingProvider()).build();

    private static ParseContext context = JsonPath.using(configuration);

    @Override
    JsonProvider<JSONObject> getProvider()
    {
        return new JsonSmartJsonProvider();
    }

    @Override
    Object get(JSONObject object, String jsonPath)
    {
        return context.parse(object).read(jsonPath);
    }

    @Override
    void assertElement(Object expected, Object actual)
    {
        assertEquals(expected, actual);
    }

    @Override
    void assertArray(List<?> expected, JSONObject result, String jsonPath)
    {
        assertArray(expected, result, jsonPath, true);
    }

    @Override
    void assertArray(List<?> expected, JSONObject result, String jsonPath, boolean exactSize)
    {
        JSONArray array = (JSONArray) get(result, jsonPath);
        assertArray(expected, array, exactSize);
    }

    private void assertArray(List<?> expected, JSONArray array, boolean exactSize)
    {
        if (exactSize)
        {
            assertElement(expected.size(), array.size());
        }
        assertTrue(array.containsAll(expected));
    }

    @Override
    Class<JSONObject> getObjectType()
    {
        return JSONObject.class;
    }

}
