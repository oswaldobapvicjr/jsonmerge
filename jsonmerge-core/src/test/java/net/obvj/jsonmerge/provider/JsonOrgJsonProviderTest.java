package net.obvj.jsonmerge.provider;

import static org.junit.jupiter.api.Assertions.*;
import static java.util.Arrays.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/***
 * Unit tests for the {@link JsonOrgJsonProvider}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 */
class JsonOrgJsonProviderTest
{
    private static final String KEY1 = "key1";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final JSONArray ARRAY1 = new JSONArray();

    static
    {
        ARRAY1.put("element1");
        ARRAY1.put("element2");
    }

    private static final JSONObject JSON1 = new JSONObject("{\n"
            + "  \"matrix\": [\n"
            + "    [11,12],\n"
            + "    [21,22]\n"
            + "  ],\n"
            + "  \"objects\": [\n"
            + "    {\n"
            + "      \"key\": \"key1\",\n"
            + "      \"value\": \"value1\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"key\": \"key2\",\n"
            + "      \"value\": \"value2\"\n"
            + "    }\n"
            + "  ]\n"
            + "}");

    private JsonProvider provider = new JsonOrgJsonProvider();

    @Test
    void newJsonArray_emptyJsonArray()
    {
        assertTrue(new JSONArray().similar(provider.newJsonArray()));
    }

    @Test
    void newJsonArray_sourceJsonArray_copy()
    {
        JSONArray result = (JSONArray) provider.newJsonArray(ARRAY1);
        assertTrue(result.similar(ARRAY1));
    }

    @Test
    void putIfAbsent_existingKey_noOverwriting()
    {
        JSONObject json = new JSONObject();
        provider.putIfAbsent(json, KEY1, VALUE1);
        provider.putIfAbsent(json, KEY1, VALUE2);
        assertEquals(VALUE1, json.get(KEY1));
    }

    @Test
    void arrayContains_existingPrimitive_true()
    {
        assertTrue(provider.arrayContains(ARRAY1, "element1"));
    }

    @Test
    void arrayContains_nonExistingPrimitive_false()
    {
        assertFalse(provider.arrayContains(ARRAY1, "element3"));
    }

    @Test
    void arrayContains_existingJSONArray_true()
    {
        assertTrue(provider.arrayContains(JSON1.getJSONArray("matrix"), new JSONArray(asList(11, 12))));
    }

    @Test
    void arrayContains_nonExistingJSONArray_true()
    {
        assertFalse(provider.arrayContains(JSON1.getJSONArray("matrix"), new JSONArray(asList(1, 2))));
    }

    @Test
    void arrayContains_existingJSONObject_true()
    {
        String expectedJson = "{\n"
                            + "  \"key\": \"key1\",\n"
                            + "  \"value\": \"value1\"\n"
                            + "}";
        assertTrue(provider.arrayContains(JSON1.getJSONArray("objects"), new JSONObject(expectedJson)));
    }

    @Test
    void arrayContains_nonExistingJSONObject_true()
    {
        String unexpectedJson = "{\n"
                              + "  \"key\": \"key1\",\n"
                              + "  \"value\": \"value9999\"\n"
                              + "}";
        assertFalse(provider.arrayContains(JSON1.getJSONArray("objects"), new JSONObject(unexpectedJson)));
    }

}
