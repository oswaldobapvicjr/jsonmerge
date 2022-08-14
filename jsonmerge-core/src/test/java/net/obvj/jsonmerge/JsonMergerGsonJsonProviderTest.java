package net.obvj.jsonmerge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
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
    JsonProvider getProvider()
    {
        return new GsonJsonProvider();
    }

    @Override
    JsonObject fromString(String string)
    {
        try
        {
            return gson.fromJson(string, JsonObject.class);
        }
        catch (Exception e)
        {
            throw new AssertionError("Unable to parse JSON string", e);
        }
    }

    @Override
    JsonObject fromFile(String path)
    {
        try
        {
            URL resource = JsonMerger.class.getClassLoader().getResource(path);
            Reader reader = new InputStreamReader(resource.openStream());
            return gson.fromJson(reader, JsonObject.class);
        }
        catch (Exception e)
        {
            throw new AssertionError("Unable to load JSON file", e);
        }
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
        if (exactSize)
        {
            assertEquals(expected.size(), array.size());
        }

        expected.forEach(element -> assertTrue(array.contains(gson.toJsonTree(element))));

    }

}
