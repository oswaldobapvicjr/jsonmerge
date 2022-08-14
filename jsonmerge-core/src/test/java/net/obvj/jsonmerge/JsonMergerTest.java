package net.obvj.jsonmerge;

import static java.util.Arrays.asList;
import static net.obvj.jsonmerge.JsonMergeOption.distinctKey;
import static net.obvj.jsonmerge.JsonMergeOption.distinctKeys;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.obvj.jsonmerge.provider.JsonProvider;

/**
 * Unit tests for the {@link OConfigurationMerger}.
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

    private static final List<Integer> EXPECTED_JSON_1_JSON_2_ARRAY = asList(1, 2, 3, 4, 5);

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

    private static final List<String> EXPECTED_JSON_3_JSON_4_DESCRIPTIONS = asList("Json3Agent1",
            "Json3Agent2", "Json4Agent1");

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

    abstract JsonProvider getProvider();

    abstract O fromString(String string);

    abstract O fromFile(String path);

    abstract Object get(O object, String jsonPath);

    abstract void assertArray(List<?> expected, O result, String jsonPath);

    abstract void assertArray(List<?> expected, O result, String jsonPath, boolean exactSize);

    /*
     * Test methods - START
     */

    @Test
    void merge_json1HighWithJson2Low_success()
    {
        O result = merger.merge(fromString(JSON_1), fromString(JSON_2));

        assertEquals("value1", get(result, "string")); // from JSON_1
        assertEquals("alt1", get(result, "alt")); // from JSON_1
        assertEquals(9876, get(result, "number")); // from JSON_2
        assertArray(EXPECTED_JSON_1_JSON_2_ARRAY, result, "array");

        assertEquals("Json1ObjectA", get(result, "$.object.a")); // from JSON_1
        assertEquals("Json1ObjectB", get(result, "$.object.b")); // from JSON_1
        assertEquals("Json2ObjectC", get(result, "$.object.c")); // from JSON_2
    }

    @Test
    void merge_json1LowWithJson2High_success()
    {
        O result = merger.merge(fromString(JSON_2), fromString(JSON_1));

        assertEquals("value2", get(result, "string")); // from JSON_2
        assertEquals("alt1", get(result, "alt")); // from JSON_1
        assertEquals(9876, get(result, "number")); // from JSON_2
        assertArray(EXPECTED_JSON_1_JSON_2_ARRAY, result, "array");

        assertEquals("Json2ObjectA", get(result, "$.object.a")); // from JSON_2
        assertEquals("Json1ObjectB", get(result, "$.object.b")); // from JSON_1
        assertEquals("Json2ObjectC", get(result, "$.object.c")); // from JSON_2
    }

    @Test
    void merge_json3HighWithJson4Low_success()
    {
        O result = merger.merge(fromString(JSON_3), fromString(JSON_4));

        assertEquals(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(EXPECTED_JSON_3_JSON_4_DESCRIPTIONS, result, "$.agents[*].description");
    }

    @Test
    void merge_json3LowWithJson4High_success()
    {
        O result = merger.merge(fromString(JSON_4), fromString(JSON_3));

        assertEquals(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(EXPECTED_JSON_3_JSON_4_DESCRIPTIONS, result, "$.agents[*].description");
    }

    @Test
    void merge_json3HighWithJson4LowAndDistinctKey_success()
    {
        O result = merger.merge(fromString(JSON_3), fromString(JSON_4),
                distinctKey("$.agents", "class"));

        assertEquals(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(asList("Json3Agent1", "Json3Agent2"), result, "$.agents[*].description");
    }

    @Test
    void merge_json3LowWithJson4HighAndDistinctKey_success()
    {
        O result = merger.merge(fromString(JSON_4), fromString(JSON_3),
                distinctKey("$.agents", "class"));

        assertEquals(Boolean.TRUE, get(result, "enabled")); // from JSON_4
        assertArray(asList("Json4Agent1", "Json3Agent2"), result, "$.agents[*].description");
    }

    @Test
    void merge_json5HighWithJson6Low_success()
    {
        O result = merger.merge(fromString(JSON_5), fromString(JSON_6));

        assertArray(asList("0123-4567-8888"), result, "$.phoneNumbers[?(@.type=='mobile')].number");
        assertArray(asList("0123-4567-8910"), result, "$.phoneNumbers[?(@.type=='home')].number");
        assertArray(asList("0123-4567-9999"), result, "$.phoneNumbers[?(@.type=='work')].number");
        assertEquals("630-0192", get(result, "$.address.postalCode"));
    }

    @Test
    void merge_json5LowWithJson6High_success()
    {
        O result = merger.merge(fromString(JSON_6), fromString(JSON_5));

        assertArray(asList("0123-4567-8888"), result, "$.phoneNumbers[?(@.type=='mobile')].number");
        assertArray(asList("0123-4567-8910"), result, "$.phoneNumbers[?(@.type=='home')].number");
        assertArray(asList("0123-4567-9999"), result, "$.phoneNumbers[?(@.type=='work')].number");
        assertEquals("630-0192", get(result, "$.address.postalCode"));
    }

    @Test
    void merge_json5HighWithJson7Low_success()
    {
        assertEquals("630-0192",
                get(merger.merge(fromString(JSON_5), fromString(JSON_7)),
                        "$.address.postalCode"));
    }

    @Test
    void merge_json5LowWithJson7High_success()
    {
        assertEquals("123 Street",
                get(merger.merge(fromString(JSON_7), fromString(JSON_8)),
                        "$.address"));
    }

    @Test
    void merge_json8HighWithJson9LowAndDistinctKey_success()
    {
        O result = merger.merge(fromString(JSON_8), fromString(JSON_9),
                distinctKey("$.array", "name"));

        assertArray(asList("Json8Value1"), result, "$.array[?(@.name=='name1')].value");
        assertArray(asList("element1", "element2"), result, "$.array[*]", false);
    }

    @Test
    void merge_json8LowWithJson9HighAndDistinctKey_success()
    {
        O result = merger.merge(fromString(JSON_9), fromString(JSON_8),
                distinctKey("$.array", "name"));

        assertArray(asList("Json9Value1"), result, "$.array[?(@.name=='name1')].value");
        assertArray(asList("element1", "element2"), result, "$.array[*]", false);
    }

    @Test
    void merge_json8LowWithJson9HighAndUnknownDistinctKey_success()
    {
        O result = merger.merge(fromString(JSON_9), fromString(JSON_8),
                distinctKey("$.array", "unknown"));

        // No exception expected, but the merge will consider no distinct key
        assertArray(asList("Json9Value1", "Json8Value1"), result,
                "$.array[?(@.name=='name1')].value");
    }

    @Test
    void merge_jsonFilesWithTwoDistinctKeys_success()
    {
        O result = merger.merge(
                fromFile("testfiles/drive2.json"),
                fromFile("testfiles/drive1.json"),
                distinctKeys("$.files", "id", "version"));

        assertArray(asList("1", "2", "3"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d')].version");

        assertArray(asList("1", "2"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002')].version");

        // drive2.json
        assertArray(asList("2017-07-07T10:14:59"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002' && @.version=='1')].date");
    }

    @Test
    void merge_jsonFilesWithTwoDistinctKeysAlt_success()
    {
        O result = merger.merge(
                fromFile("testfiles/drive1.json"),
                fromFile("testfiles/drive2.json"),
                distinctKeys("$.files", "id", "version"));

        assertArray(asList("1", "2", "3"), result,
                "$.files[?(@.id=='d2b638be-40d2-4965-906e-291521f8a19d')].version");

        assertArray(asList("1", "2"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002')].version");

        // drive1.json
        assertArray(asList("2022-08-06T09:51:40"), result,
                "$.files[?(@.id=='9570cc646-1586-11ed-861d-0242ac120002' && @.version=='1')].date");
    }
}
