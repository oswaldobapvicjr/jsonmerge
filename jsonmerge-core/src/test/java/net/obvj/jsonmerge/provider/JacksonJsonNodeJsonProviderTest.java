package net.obvj.jsonmerge.provider;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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

    private JsonProvider provider = new JacksonJsonNodeJsonProvider();

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

}
