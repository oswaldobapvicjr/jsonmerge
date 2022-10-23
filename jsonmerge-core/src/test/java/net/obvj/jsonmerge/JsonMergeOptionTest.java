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

import static java.util.Collections.emptyList;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link JsonMergeOption}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class JsonMergeOptionTest
{

    @Test
    void toString_AllParamsSet_success()
    {
        assertThat(JsonMergeOption.onPath("$.myPath").findObjectsIdentifiedBy("key1", "key2")
                        .thenDoADeepMerge().toString(),
                containsAll("JsonMergeOption", "path=$['myPath']", "keys=[key1, key2]",
                        "deepMerge=true", "distinctObjectsOnly=true"));

        assertThat(JsonMergeOption.onPath("$.myPath").addAll().toString(),
                containsAll("JsonMergeOption", "path=$['myPath']", "keys=[]", "deepMerge=false",
                        "distinctObjectsOnly=false"));
    }

    @Test
    void default_expectedParameters()
    {
        assertThat(JsonMergeOption.DEFAULT.getPath().toString(), equalTo("@"));
        assertThat(JsonMergeOption.DEFAULT.getKeys(), equalTo(emptyList()));
        assertThat(JsonMergeOption.DEFAULT.isDeepMerge(), equalTo(false));
        assertThat(JsonMergeOption.DEFAULT.isDistinctObjectsOnly(), equalTo(true));
    }

    @Test
    void equals_similarObjects_true()
    {
        assertEquals(
                JsonMergeOption.onPath("path1").findObjectsIdentifiedBy("key1").thenDoADeepMerge(),
                JsonMergeOption.onPath("path1").findObjectsIdentifiedBy("key2")
                        .thenPickTheHighestPrecedenceOne());
    }

    @Test
    void equals_sameObject_true()
    {
        JsonMergeOption option = JsonMergeOption.onPath("pathA").addAll();
        assertEquals(option, option);
    }

    @Test
    void equals_null_false()
    {
        assertFalse(JsonMergeOption.onPath("pathB").addDistinctObjectsOnly().equals(null));
    }

    @Test
    void equals_differentObjects_false()
    {
        JsonMergeOption option = JsonMergeOption.onPath("pathC").addAll();
        assertFalse(option.equals(JsonMergeOption.onPath("pathD").addAll()));
        assertFalse(option.equals(new Object()));
    }

    @Test
    void equals_similarObjectsInAHashSet_noRepeatedElements()
    {
        List<JsonMergeOption> list = Arrays.asList(JsonMergeOption.onPath("pathE").addAll(),
                JsonMergeOption.onPath("pathE").addDistinctObjectsOnly());
        Set<JsonMergeOption> set = new HashSet<>(list);

        assertEquals(1, set.size());
        assertTrue(set.containsAll(list));
    }

}
