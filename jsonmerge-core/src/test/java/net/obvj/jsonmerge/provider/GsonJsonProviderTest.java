package net.obvj.jsonmerge.provider;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Unit tests for the {@link GsonJsonProvider}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 */
class GsonJsonProviderTest
{
    private static final String KEY1 = "key1";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final JsonArray ARRAY1 = new JsonArray();

    static
    {
        ARRAY1.add("element1");
        ARRAY1.add("element2");
    }

    private GsonJsonProvider provider = new GsonJsonProvider();

    @Test
    void newJsonArray_emptyJsonArray()
    {
        assertEquals(new JsonArray(), provider.newJsonArray());
    }

    @Test
    void newJsonArray_sourceJsonArray_copy()
    {
        Object result = provider.newJsonArray(ARRAY1);
        assertEquals(ARRAY1, result);
        assertNotSame(ARRAY1, result);
    }

    @Test
    void putIfAbsent_existingKey_noOverwriting()
    {
        JsonObject json = new JsonObject();
        provider.putIfAbsent(json, KEY1, VALUE1);
        provider.putIfAbsent(json, KEY1, VALUE2);
        assertEquals(VALUE1, json.get(KEY1).getAsString());
    }

    @Test
    void put_object_convertedToJsonElement()
    {
        JsonObject json = new JsonObject();
        provider.put(json, KEY1, VALUE1);
        assertEquals(VALUE1, json.get(KEY1).getAsString());
    }

    @Test
    void put_jsonElement_noConversion()
    {
        JsonObject json = new JsonObject();
        provider.put(json, KEY1, provider.toJsonElement(VALUE1));
        assertEquals(VALUE1, json.get(KEY1).getAsString());
    }

}
