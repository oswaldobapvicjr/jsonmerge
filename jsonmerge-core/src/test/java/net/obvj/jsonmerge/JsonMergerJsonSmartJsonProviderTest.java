package net.obvj.jsonmerge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
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
    JsonProvider getProvider()
    {
        return new JsonSmartJsonProvider();
    }

    @Override
    JSONObject fromString(String string)
    {
        try
        {
            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return parser.parse(string, JSONObject.class);
        }
        catch (Exception e)
        {
            throw new AssertionError("Unable to parse JSON string", e);
        }
    }

    @Override
    JSONObject fromFile(String path)
    {
        try
        {
            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            URL resource = JsonMergerTest.class.getClassLoader().getResource(path);
            return parser.parse(resource.openStream(), JSONObject.class);
        }
        catch (Exception e)
        {
            throw new AssertionError("Unable to load JSON file", e);
        }
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

}
