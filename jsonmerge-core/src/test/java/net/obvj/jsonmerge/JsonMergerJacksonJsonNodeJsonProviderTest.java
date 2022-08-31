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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import net.obvj.jsonmerge.provider.JacksonJsonNodeJsonProvider;

/**
 * Unit tests for the {@link JsonMerger} using the {@link JacksonJsonNodeJsonProvider}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class JsonMergerJacksonJsonNodeJsonProviderTest extends JsonMergerTest<JsonNode>
{

    private static Configuration configuration = Configuration.builder()
            .jsonProvider(new com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider()).build();

    private static ParseContext context = JsonPath.using(configuration);

    @Override
    JacksonJsonNodeJsonProvider getProvider()
    {
        return new JacksonJsonNodeJsonProvider();
    }

    @Override
    Object get(JsonNode object, String jsonPath)
    {
        return context.parse(object).read(jsonPath);
    }

    @Override
    void assertElement(Object expected, Object actual)
    {
        assertEquals(getProvider().toJsonNode(expected), actual);
    }

    @Override
    void assertArray(List<?> expected, JsonNode result, String jsonPath)
    {
        assertArray(expected, result, jsonPath, true);
    }

    @Override
    void assertArray(List<?> expected, JsonNode result, String jsonPath, boolean exactSize)
    {
        ArrayNode array = (ArrayNode) get(result, jsonPath);
        assertArray(expected, array, exactSize);
    }

    private void assertArray(List<?> expected, ArrayNode array, boolean exactSize)
    {
        if (exactSize)
        {
            assertEquals(expected.size(), array.size());
        }

        expected.forEach(expectedElement ->
        {
            assertTrue(
                    getProvider().stream(array)
                            .anyMatch(getProvider().toJsonNode(expectedElement)::equals),
                    () -> String.format("Expected element %s not found", expectedElement));
        });
    }

}
