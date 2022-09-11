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

package net.obvj.jsonmerger.testdrive;

import static net.obvj.jsonmerge.JsonMergeOption.onPath;

import org.json.JSONObject;

import net.obvj.jsonmerge.JsonMerger;
import net.obvj.jsonmerge.provider.VertxJsonProvider;

/**
 * Test-drive class for {@link JsonMerger} using the {@link VertxJsonProvider}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
public class JsonMergerJsonOrgTestDrive
{
    public static void main(String[] args)
    {
        String json1 = "{\n"
                + "  \"firstName\": \"John\",\n"
                + "  \"lastName\": \"Doe\",\n"
                + "  \"phoneNumbers\": [\n"
                + "    {\n"
                + "      \"type\": \"home\",\n"
                + "      \"number\": \"0123-4567-8910\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"type\": \"mobile\",\n"
                + "      \"number\": \"9876-5432-1000\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";

        String json2 = "{\n"
                + "  \"firstName\": \"John\",\n"
                + "  \"lastName\": \"Doe\",\n"
                + "  \"age\": 26,\n"
                + "  \"phoneNumbers\": [\n"
                + "    {\n"
                + "      \"type\": \"home\",\n"
                + "      \"number\": \"0123-4567-8910\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";

        JsonMerger<JSONObject> merger = new JsonMerger<>(JSONObject.class);

        JSONObject merged = merger.merge(json1, json2,
                onPath("phoneNumbers").findObjectsIdentifiedBy("type").thenDoADeepMerge());

        System.out.println(merged.toString(2));
    }
}
