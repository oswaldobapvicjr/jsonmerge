package net.obvj.jsonmerge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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
    JsonProvider getProvider()
    {
        return new JsonOrgJsonProvider();
    }

    @Override
    JSONObject fromString(String string)
    {
        try
        {
            JSONTokener tokener = new JSONTokener(string);
            return new JSONObject(tokener);
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
            URL resource = JsonMergerTest.class.getClassLoader().getResource(path);
            JSONTokener tokener = new JSONTokener(resource.openStream());
            return new JSONObject(tokener);
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

}
