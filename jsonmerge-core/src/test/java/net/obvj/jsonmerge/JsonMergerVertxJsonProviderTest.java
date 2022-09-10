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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.obvj.jsonmerge.provider.VertxJsonProvider;
import net.obvj.jsonmerge.support.jsonpath.VertxMappingProvider;
import net.obvj.jsonmerge.provider.JsonProvider;

/**
 * Unit tests for the {@link JsonMerger} using the {@link VertxJsonProvider}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
class JsonMergerVertxJsonProviderTest extends JsonMergerTest<JsonObject>
{

    private static Configuration configuration = Configuration.builder()
            .jsonProvider(new net.obvj.jsonmerge.support.jsonpath.VertxJsonProvider())
            .mappingProvider(new VertxMappingProvider()).build();

    private static ParseContext context = JsonPath.using(configuration);

    @Override
    JsonProvider<JsonObject> getProvider()
    {
        return new VertxJsonProvider();
    }

    @Override
    Object get(JsonObject object, String jsonPath)
    {
        return context.parse(object).read(jsonPath);
    }

    @Override
    void assertElement(Object expected, Object actual)
    {
        assertEquals(expected, actual);
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
        expected.forEach(element -> assertTrue(array.contains(element),
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
