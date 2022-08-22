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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static java.util.Arrays.*;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;

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
