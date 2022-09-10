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

import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;

import net.obvj.jsonmerge.provider.JsonOrgJsonProvider;
import net.obvj.jsonmerge.provider.JsonProvider;

/**
 * Unit tests for the {@link JsonMerger} using the {@link JsonOrgJsonProvider}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class JsonMergerJsonOrgJsonProviderTest extends JsonMergerTest<JSONObject>
{

    private static Configuration configuration = Configuration.builder()
            .jsonProvider(new com.jayway.jsonpath.spi.json.JsonOrgJsonProvider())
            .mappingProvider(new JsonOrgMappingProvider()).build();

    private static ParseContext context = JsonPath.using(configuration);

    @Override
    JsonProvider<JSONObject> getProvider()
    {
        return new JsonOrgJsonProvider();
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
            assertElement(expected.size(), array.length());
        }

        expected.forEach(expectedElement ->
        {
            assertTrue(
                    getProvider().stream(array)
                            .anyMatch(arrayElement -> arrayElement.equals(expectedElement)),
                    () -> String.format("Expected element %s not found", expectedElement));
        });

    }

    @Override
    Class<JSONObject> getObjectType()
    {
        return JSONObject.class;
    }

}
