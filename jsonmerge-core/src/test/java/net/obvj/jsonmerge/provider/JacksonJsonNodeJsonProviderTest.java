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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Unit tests for the {@link JacksonJsonNodeJsonProvider} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
class JacksonJsonNodeJsonProviderTest
{
    private static final ArrayNode ARRAY1 = JsonNodeFactory.instance.arrayNode();

    static
    {
        ARRAY1.add("element1");
        ARRAY1.add("element2");
    }

    private JsonProvider<JsonNode> provider = new JacksonJsonNodeJsonProvider();

    @Test
    void newJsonArray_emptyJsonArray()
    {
        assertEquals(JsonNodeFactory.instance.arrayNode(), provider.newJsonArray());
    }

    @Test
    void newJsonArray_sourceJsonArray_copy()
    {
        Object result = provider.newJsonArray(ARRAY1);
        assertEquals(ARRAY1, result);
        assertNotSame(ARRAY1, result);
    }

    @Test
    void indexOf_success()
    {
        assertEquals(0, provider.indexOf(ARRAY1, "element1"));
        assertEquals(1, provider.indexOf(ARRAY1, "element2"));
        assertEquals(-1, provider.indexOf(ARRAY1, "unknown"));
    }

    @Test
    void stream_array_allElementsCopied()
    {
        StringBuilder sb = new StringBuilder();
        provider.stream(ARRAY1).forEach(sb::append);
        assertThat(sb.toString(), containsAll("element1", "element2"));
    }

}
