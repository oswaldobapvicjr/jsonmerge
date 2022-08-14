package net.obvj.jsonmerge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
class JsonMergerJacksonJsonNodeJsonProviderTest extends JsonMergerTest<ObjectNode>
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
    ObjectNode fromString(String string)
    {
        try
        {
            return new JsonMapper().readValue(string, ObjectNode.class);
        }
        catch (Exception e)
        {
            throw new AssertionError("Unable to parse JSON string", e);
        }
    }

    @Override
    ObjectNode fromFile(String path)
    {
        try
        {
            URL resource = JsonMerger.class.getClassLoader().getResource(path);
            return new JsonMapper().readValue(resource.openStream(), ObjectNode.class);
        }
        catch (Exception e)
        {
            throw new AssertionError("Unable to load JSON file", e);
        }
    }

    @Override
    Object get(ObjectNode object, String jsonPath)
    {
        return context.parse(object).read(jsonPath);
    }

    @Override
    void assertElement(Object expected, Object actual)
    {
        assertEquals(getProvider().toJsonNode(expected), actual);
    }

    @Override
    void assertArray(List<?> expected, ObjectNode result, String jsonPath)
    {
        assertArray(expected, result, jsonPath, true);
    }

    @Override
    void assertArray(List<?> expected, ObjectNode result, String jsonPath, boolean exactSize)
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
