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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertSame;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;

import net.minidev.json.JSONObject;

class JsonProviderFactoryTest
{
    private JsonProviderFactory factory = JsonProviderFactory.instance();

    @Test
    void getByType_validTypes_success()
    {
        assertThat(factory.getByType(JSONObject.class),
                is(equalTo(new JsonSmartJsonProvider())));
        assertThat(factory.getByType(JsonObject.class),
                is(equalTo(new GsonJsonProvider())));
        assertThat(factory.getByType(JsonNode.class),
                is(equalTo(new JacksonJsonNodeJsonProvider())));
        assertThat(factory.getByType(ObjectNode.class),
                is(equalTo(new JacksonJsonNodeJsonProvider())));
        assertThat(factory.getByType(org.json.JSONObject.class),
                is(equalTo(new JsonOrgJsonProvider())));
        assertThat(factory.getByType(io.vertx.core.json.JsonObject.class),
                is(equalTo(new VertxJsonProvider())));
    }

    @Test
    void getByType_invalidType_illegalArgumentException()
    {
        assertThat(() -> factory.getByType(Integer.class),
                throwsException(IllegalArgumentException.class)
                        .withMessage("No JsonProvider available for java.lang.Integer"));
    }

    @Test
    void getByType_null_npeWithProperMessage()
    {
        assertThat(() -> factory.getByType(null), throwsException(NullPointerException.class)
                .withMessage("The search type must not be null"));
    }

    @Test
    void instance_sameInstanceAndNotNull()
    {
        assertSame(factory, JsonProviderFactory.instance());
    }

}
