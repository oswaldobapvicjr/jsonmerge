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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.obvj.jsonmerge.JsonMergeOption.onPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.obvj.jsonmerge.provider.JsonProvider;

/**
 * Unit tests for the {@link JsonMerger}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
abstract class JsonMergerTest<O>
{

    private static final String JSON_1
            = "{\n"
            + "  \"string\": \"value1\",\n"
            + "  \"array\": [1, 2, 3],\n"
            + "  \"object\": {\n"
            + "    \"a\": \"Json1ObjectA\",\n"
            + "    \"b\": \"Json1ObjectB\"\n"
            + "  }\n,"
            + "  \"alt\": \"alt1\""
            + "}";

    private static final String JSON_2
            = "{\n"
            + "  \"string\": \"value2\",\n"
            + "  \"array\": [3, 4, 5],\n"
            + "  \"object\": {\n"
            + "    \"a\": \"Json2ObjectA\",\n"
            + "    \"c\": \"Json2ObjectC\"\n"
            + "  }\n,"
            + "  \"number\": 9876"
            + "}";

    private static final List<Integer> EXPECTED_JSON_1_JSON_2_ARRAY_DISTINCT = asList(1, 2, 3, 4, 5);

    private static final String JSON_3
            = "{\n"
            + "  \"agents\": [\n"
            + "    {\n"
            + "      \"class\": \"Agent1\",\n"
            + "      \"description\": \"Json3Agent1\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"class\": \"Agent2\",\n"
            + "      \"description\": \"Json3Agent2\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    private static final String JSON_4
            = "{\n"
            + "  \"enabled\": true,\n"
            + "  \"agents\": [\n"
            + "    {\n"
            + "      \"class\": \"Agent1\",\n"
            + "      \"description\": \"Json4Agent1\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    private static final List<String> EXPECTED_JSON_3_JSON_4_DESCRIPTIONS_DISTINCT = asList(
            "Json3Agent1", "Json3Agent2", "Json4Agent1");

    private static final String JSON_5
            = "{\r\n"
            + "  \"name\": \"John\",\r\n"
            + "  \"address\": {\r\n"
            + "    \"postalCode\": \"630-0192\"\r\n"
            + "  },\r\n"
            + "  \"phoneNumbers\": [\r\n"
            + "    {\r\n"
            + "      \"type\": \"mobile\",\r\n"
            + "      \"number\": \"0123-4567-8888\"\r\n"
            + "    },\r\n"
            + "    {\r\n"
            + "      \"type\": \"home\",\r\n"
            + "      \"number\": \"0123-4567-8910\"\r\n"
            + "    }\r\n"
            + "  ]\r\n"
            + "}";

    private static final String JSON_6
            = "{\r\n"
            + "  \"name\": \"John\",\r\n"
            + "  \"address\": {},\r\n"
            + "  \"phoneNumbers\": [\r\n"
            + "    {\r\n"
            + "      \"type\": \"mobile\",\r\n"
            + "      \"number\": \"0123-4567-8888\"\r\n"
            + "    },\r\n"
            + "    {\r\n"
            + "      \"type\": \"work\",\r\n"
            + "      \"number\": \"0123-4567-9999\"\r\n"
            + "    }\r\n"
            + "  ]\r\n"
            + "}";

    private static final String JSON_7
            = "{\r\n"
            + "  \"address\": \"123 Street\"\r\n"
            + "}";

    private static final String JSON_8
            = "{\r\n"
            + "  \"array\": [\r\n"
            + "    {\r\n"
            + "      \"name\": \"name1\",\r\n"
            + "      \"value\": \"Json8Value1\"\r\n"
            + "    },\r\n"
            + "    \"element1\""
            + "  ]\r\n"
            + "}";

    private static final String JSON_9
            = "{\r\n"
            + "  \"array\": [\r\n"
            + "    {\r\n"
            + "      \"name\": \"name1\",\r\n"
            + "      \"value\": \"Json9Value1\"\r\n"
            + "    },\r\n"
            + "    \"element1\","
            + "    \"element2\""
            + "  ]\r\n"
            + "}";

    private final JsonMerger<O> merger = new JsonMerger<O>(getProvider());

    /*
     * Utility methods - START
     */

    abstract JsonProvider<O> getProvider();

    abstract Object get(O object, String jsonPath);

    abstract void assertElement(Object expected, Object actual);

    private void assertElement(Object expected, O result, String jsonPath)
    {
        assertArray(asList(expected), result, jsonPath);
    }

    abstract void assertArray(List<?> expected, O result, String jsonPath);

    abstract void assertArray(List<?> expected, O result, String jsonPath, boolean exactSize);

    private O fromString(String string)
    {
        return getProvider().parse(string);
    }

    private O fromFile(String path)
    {
        try
        {
            URL resource = JsonMerger.class.getClassLoader().getResource(path);
            return getProvider().parse(resource.openStream());
        }
        catch (Exception exception)
        {
            throw new AssertionError("Unable to load JSON file", exception);
        }
    }

    abstract Class<O> getObjectType();

    /*
     * Test methods - START
     */

    @Test
    void constructor_validType_validProvider()
    {
        JsonMerger<O> merger = new JsonMerger<O>(getObjectType());
        assertEquals(getProvider(), merger.getJsonProvider());
    }

    @Test
    void merge_json1HighWithJson2LowDefaultOption_success()
    {
        O result = merger.merge(fromString(JSON_1), fromString(JSON_2));

        assertElement("value1", get(result, "string")); // from JSON_1
        assertElement("alt1", get(result, "alt")); // from JSON_1
        assertElement(9876, get(result, "number")); // from JSON_2
        assertArray(EXPECTED_JSON_1_JSON_2_ARRAY_DISTINCT, result, "array");

        assertElement("Json1ObjectA", get(result, "$.object.a")); // from JSON_1
        assertElement("Json1ObjectB", get(result, "$.object.b")); // from JSON_1
        assertElement("Json2ObjectC", get(result, "$.object.c")); // from JSON_2
    }

    @Test
    void merge_json1HighWithJson2LowBothPassedAsStringsAndNoMergeOption_success()
    {
        O result = merger.merge(JSON_1, JSON_2);

        assertElement("value1", get(result, "string")); // from JSON_1
        assertElement("alt1", get(result, "alt")); // from JSON_1
        assertElement(9876, get(result, "number")); // from JSON_2
        assertArray(EXPECTED_JSON_1_JSON_2_ARRAY_DISTINCT, result, "array");

        assertElement("Json1ObjectA", get(result, "$.object.a")); // from JSON_1
        assertElement("Json1ObjectB", get(result, "$.object.b")); // from JSON_1
        assertElement("Json2ObjectC", get(result, "$.object.c")); // from JSON_2
    }

    @Test
    void merge_json1LowWithJson2HighDefaultOption_success()
    {
        O result = merger.merge(fromString(JSON_2), fromString(JSON_1));

        assertElement("value2", get(result, "string")); // from JSON_2
        assertElement("alt1", get(result, "alt")); // from JSON_1
        assertElement(9876, get(result, "number")); // from JSON_2
        assertArray(EXPECTED_JSON_1_JSON_2_ARRAY_DISTINCT, result, "array");

        assertElement("Json2ObjectA", get(result, "$.object.a")); // from JSON_2
        assertElement("Json1ObjectB", get(result, "$.object.b")); // from JSON_1
        assertElement("Json2ObjectC", get(result, "$.object.c")); // from JSON_2
    }

    @Test
    void merge_json1HighWithJson2LowWithOptionAddDistinctObjectsOnly_success()
    {
        O result = merger.merge(fromString(JSON_1), fromString(JSON_2),
                JsonMergeOption.onPath("array").addDistinctObjectsOnly());

        assertElement(1, get(result, "$.array[0]"));
        assertElement(2, get(result, "$.array[1]"));
        assertElement(3, get(result, "$.array[2]"));
        assertElement(4, get(result, "$.array[3]"));
        assertElement(5, get(result, "$.array[4]"));
    }

    @Test
    void merge_json1HighWithJson2LowWithOptionAddAllOnArray_success()
    {
        O result = merger.merge(fromString(JSON_1), fromString(JSON_2),
                JsonMergeOption.onPath("array").addAll());

        assertElement(1, get(result, "$.array[0]"));
        assertElement(2, get(result, "$.array[1]"));
        assertElement(3, get(result, "$.array[2]"));
        assertElement(3, get(result, "$.array[3]"));
        assertElement(4, get(result, "$.array[4]"));
        assertElement(5, get(result, "$.array[5]"));
    }

    @Test
    void merge_json1HighWithJson2LowBothPassedAsStringsWithOptionAddAllOnArray_success()
    {
        O result = merger.merge(JSON_1, JSON_2,
                JsonMergeOption.onPath("array").addAll());

        assertElement(1, get(result, "$.array[0]"));
        assertElement(2, get(result, "$.array[1]"));
        assertElement(3, get(result, "$.array[2]"));
        assertElement(3, get(result, "$.array[3]"));
        assertElement(4, get(result, "$.array[4]"));
        assertElement(5, get(result, "$.array[5]"));
    }

    @Test
    void merge_json3HighWithJson4LowDefaultOption_success()
    {
        O result = merger.merge(fromString(JSON_3), fromString(JSON_4));

        assertElement(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(EXPECTED_JSON_3_JSON_4_DESCRIPTIONS_DISTINCT, result,
                "$.agents[*].description");
    }

    @Test
    void merge_json3LowWithJson4HighDefaultOption_success()
    {
        O result = merger.merge(fromString(JSON_4), fromString(JSON_3));

        assertElement(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(EXPECTED_JSON_3_JSON_4_DESCRIPTIONS_DISTINCT, result,
                "$.agents[*].description");
    }

    /*
     * This test is to secure the coverage of the legacy JsonMergeOption.distinctKey(...)
     * method. It can be safely removed together with the associated legacy feature.
     */
    @Test
    @Deprecated
    void merge_json3HighWithJson4LowAndDistinctKey_success()
    {
        O result = merger.merge(fromString(JSON_3), fromString(JSON_4),
                JsonMergeOption.distinctKey("$.agents", "class"));

        assertElement(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(asList("Json3Agent1", "Json3Agent2"), result, "$.agents[*].description");
    }

    @Test
    void merge_json3HighWithJson4LowAndDistinctKeyAndPickTheHigherPrecedenceOne_success()
    {
        O result = merger.merge(fromString(JSON_3), fromString(JSON_4),
                onPath("$.agents").findObjectsIdentifiedBy("class")
                        .thenPickTheHighestPrecedenceOne());

        assertElement(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(asList("Json3Agent1", "Json3Agent2"), result, "$.agents[*].description");
    }

    @Test
    void merge_json3LowWithJson4HighAndDistinctKeyAndPickTheHigherPrecedenceOne_success()
    {
        O result = merger.merge(fromString(JSON_4), fromString(JSON_3),
                onPath("$.agents").findObjectsIdentifiedBy("class")
                        .thenPickTheHighestPrecedenceOne());

        assertElement(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(asList("Json4Agent1", "Json3Agent2"), result, "$.agents[*].description");
    }

    @Test
    void merge_json5HighWithJson6Low_success()
    {
        O result = merger.merge(fromString(JSON_5), fromString(JSON_6));

        assertArray(asList("0123-4567-8888"), result, "$.phoneNumbers[?(@.type=='mobile')].number");
        assertArray(asList("0123-4567-8910"), result, "$.phoneNumbers[?(@.type=='home')].number");
        assertArray(asList("0123-4567-9999"), result, "$.phoneNumbers[?(@.type=='work')].number");
        assertElement("630-0192", get(result, "$.address.postalCode"));
    }

    @Test
    void merge_json5LowWithJson6High_success()
    {
        O result = merger.merge(fromString(JSON_6), fromString(JSON_5));

        assertArray(asList("0123-4567-8888"), result, "$.phoneNumbers[?(@.type=='mobile')].number");
        assertArray(asList("0123-4567-8910"), result, "$.phoneNumbers[?(@.type=='home')].number");
        assertArray(asList("0123-4567-9999"), result, "$.phoneNumbers[?(@.type=='work')].number");
        assertElement("630-0192", get(result, "$.address.postalCode"));
    }

    @Test
    void merge_json5HighWithJson7Low_success()
    {
        assertElement("630-0192",
                get(merger.merge(fromString(JSON_5), fromString(JSON_7)),
                        "$.address.postalCode"));
    }

    @Test
    void merge_json5LowWithJson7High_success()
    {
        assertElement("123 Street",
                get(merger.merge(fromString(JSON_7), fromString(JSON_8)),
                        "$.address"));
    }

    @Test
    void merge_json8HighWithJson9LowAndDistinctKeyAndPickTheHigherPrecedenceOne_success()
    {
        O result = merger.merge(fromString(JSON_8), fromString(JSON_9),
                onPath("$.array").findObjectsIdentifiedBy("name").thenPickTheHighestPrecedenceOne());

        assertArray(asList("Json8Value1"), result, "$.array[?(@.name=='name1')].value");
        assertArray(asList("element1", "element2"), result, "$.array[*]", false);
    }

    @Test
    void merge_json8LowWithJson9HighAndDistinctKeyAndPickTheHigherPrecedenceOne_success()
    {
        O result = merger.merge(fromString(JSON_9), fromString(JSON_8),
                onPath("$.array").findObjectsIdentifiedBy("name").thenPickTheHighestPrecedenceOne());

        assertArray(asList("Json9Value1"), result, "$.array[?(@.name=='name1')].value");
        assertArray(asList("element1", "element2"), result, "$.array[*]", false);
    }

    @Test
    void merge_json8LowWithJson9HighAndUnknownDistinctKeyAndPickTheHigherPrecedenceOne_success()
    {
        O result = merger.merge(fromString(JSON_9), fromString(JSON_8),
                onPath("$.array").findObjectsIdentifiedBy("unknown")
                        .thenPickTheHighestPrecedenceOne());

        // No exception expected, but the merge will consider no distinct key
        assertArray(asList("Json9Value1", "Json8Value1"), result,
                "$.array[?(@.name=='name1')].value");
    }

    @Test
    void merge_jsonFilesWithTwoDistinctKeysThenPickTheHigherPrecedenceOne_success()
    {
        O result = merger.merge(
                fromFile("testfiles/drive2.json"),
                fromFile("testfiles/drive1.json"),
                onPath("$.files").findObjectsIdentifiedBy("id", "version")
                        .thenPickTheHighestPrecedenceOne());

        assertArray(asList("1", "2", "3"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d')].version");

        // drive2.json
        assertArray(asList("jackson", "java"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].keywords[*]");
        assertArray(asList(Boolean.TRUE), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].readOnly");

        assertArray(asList("1", "2"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002')].version");

        // drive2.json
        assertArray(asList("2017-07-07T10:14:59"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002' && @.version=='1')].date");
    }

    /*
     * This test is to secure the coverage of the legacy JsonMergeOption.distinctKey(...)
     * method. It can be safely removed together with the associated legacy feature.
     */
    @Test
    @Deprecated
    void merge_jsonFilesWithTwoDistinctKeys_success()
    {
        O result = merger.merge(
                fromFile("testfiles/drive2.json"),
                fromFile("testfiles/drive1.json"),
                JsonMergeOption.distinctKeys("$.files", "id", "version"));

        assertArray(asList("1", "2", "3"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d')].version");

        // drive2.json
        assertArray(asList("jackson", "java"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].keywords[*]");
        assertArray(asList(Boolean.TRUE), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].readOnly");

        assertArray(asList("1", "2"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002')].version");

        // drive2.json
        assertArray(asList("2017-07-07T10:14:59"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002' && @.version=='1')].date");
    }

    @Test
    void merge_jsonFilesWithTwoDistinctKeysThenPickTheHigherPrecedenceOneAlt_success()
    {
        O result = merger.merge(
                fromFile("testfiles/drive1.json"),
                fromFile("testfiles/drive2.json"),
                onPath("$.files").findObjectsIdentifiedBy("id", "version")
                        .thenPickTheHighestPrecedenceOne());

        assertArray(asList("1", "2", "3"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d')].version");

        assertArray(asList("1", "2"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002')].version");

        // drive1.json
        assertArray(asList("java", "test"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].keywords[*]");
        assertArray(emptyList(), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].readOnly");

        // drive1.json
        assertArray(asList("2022-08-06T09:51:40"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002' && @.version=='1')].date");
    }

    @Test
    void merge_jsonFilesWithTwoDistinctKeysThenDoADeepMerge_success()
    {
        O result = merger.merge(
                fromFile("testfiles/drive1.json"),
                fromFile("testfiles/drive2.json"),
                onPath("$.files").findObjectsIdentifiedBy("id", "version")
                        .thenDoADeepMerge());

        assertArray(asList("1", "2", "3"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d')].version");
        assertArray(asList("1", "2"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002')].version");

        // deep-merged elements
        assertArray(asList("jackson", "java", "test"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].keywords[*]");
        assertArray(asList(Boolean.TRUE), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d'&&@.version=='2')].readOnly");
    }

    @Test
    void merge_jsonFilesUsers_success()
    {
        O result = merger.merge(fromFile("testfiles/users1.json"),
                fromFile("testfiles/users2.json"),
                onPath("$.sites").findObjectsIdentifiedBy("id").thenDoADeepMerge(),
                onPath("$.sites[*].users").findObjectsIdentifiedBy("name").thenDoADeepMerge(),
                onPath("$.sites[*].users[*].friends").addDistinctObjectsOnly(),
                onPath("$.sites[*].users[*].preferences").findObjectsIdentifiedBy("key")
                        .thenPickTheHighestPrecedenceOne());

        // ----------------------------
        // Camille Lawson
        // ----------------------------

        assertElement("$2,114.65", result, // users1
                "$..users[?(@.name=='Camille Lawson')].balance");

        // preferences
        assertElement("pt-BR", result, // users1
                "$..users[?(@.name=='Camille Lawson')].preferences[?(@.key=='language')].value");
        assertElement("BRT", result, // users1
                "$..users[?(@.name=='Camille Lawson')].preferences[?(@.key=='timeZone')].value");
        assertElement("light high contrast", result, // users1
                "$..users[?(@.name=='Camille Lawson')].preferences[?(@.key=='theme')].value");

        // ----------------------------
        // Morrow Rasmussen
        // ----------------------------

        assertElement("$1,683.27", result, // users1
                "$..users[?(@.name=='Morrow Rasmussen')].balance");

        // friends
        assertArray(asList("Atkins Lang"), result, // users1
                "$..users[?(@.name=='Morrow Rasmussen')].friends[*].name");

        // preferences
        assertElement("pt-BR", result, // users2
                "$..users[?(@.name=='Morrow Rasmussen')].preferences[?(@.key=='language')].value");
        assertElement("BRT", result, // users2
                "$..users[?(@.name=='Morrow Rasmussen')].preferences[?(@.key=='timeZone')].value");
        assertElement("dark", result, // users1
                "$..users[?(@.name=='Morrow Rasmussen')].preferences[?(@.key=='theme')].value");

        // ----------------------------
        // Johnnie Alvarado
        // ----------------------------

        assertElement("$1,493.14", result, // users1
                "$..users[?(@.name=='Johnnie Alvarado')].balance");

        // friends
        assertArray(asList("Bertie Skinner", "Morrow Rasmussen"), result, // merged
                "$..users[?(@.name=='Johnnie Alvarado')].friends[*].name");

        // preferences
        assertElement("es-CO", result, // users1
                "$..users[?(@.name=='Johnnie Alvarado')].preferences[?(@.key=='language')].value");
        assertElement("COT", result, // users1
                "$..users[?(@.name=='Johnnie Alvarado')].preferences[?(@.key=='timeZone')].value");
        assertElement("light", result, // users1
                "$..users[?(@.name=='Johnnie Alvarado')].preferences[?(@.key=='theme')].value");

        // ----------------------------
        // Ethel Zimmerman
        // ----------------------------

        assertElement("$1,078.67", result, // users1
                "$..users[?(@.name=='Ethel Zimmerman')].balance");

        // friends
        assertArray(asList("Grant Knox", "Bertie Skinner"), result, // merged
                "$..users[?(@.name=='Ethel Zimmerman')].friends[*].name");

        // preferences
        assertElement("en-GB", result, // users1
                "$..users[?(@.name=='Ethel Zimmerman')].preferences[?(@.key=='language')].value");
        assertElement("UTC", result, // users1
                "$..users[?(@.name=='Ethel Zimmerman')].preferences[?(@.key=='timeZone')].value");
        assertElement("light high contrast", result, // users1
                "$..users[?(@.name=='Ethel Zimmerman')].preferences[?(@.key=='theme')].value");

        // ----------------------------
        // Nikki Hamilton
        // ----------------------------

        assertElement("$1,160.43", result, // users2
                "$..users[?(@.name=='Nikki Hamilton')].balance");

        // friends
        assertArray(asList("Riley Barr", "Solis English", "Velma Burton"), result, // users2
                "$..users[?(@.name=='Nikki Hamilton')].friends[*].name");

        // preferences
        assertElement("es-ES", result, // users2
                "$..users[?(@.name=='Nikki Hamilton')].preferences[?(@.key=='language')].value");
        assertElement("CET", result, // users2
                "$..users[?(@.name=='Nikki Hamilton')].preferences[?(@.key=='timeZone')].value");
        assertElement("dark", result, // users2
                "$..users[?(@.name=='Nikki Hamilton')].preferences[?(@.key=='theme')].value");

    }
}
