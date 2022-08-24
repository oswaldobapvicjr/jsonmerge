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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(
                JsonMergeOption.onPath("$.myPath").findObjectsIdentifiedBy("key1", "key2")
                        .thenDoADeepMerge().toString(),
                containsAll("JsonMergeOption", "path=$['myPath']", "keys=[key1, key2]",
                        "deepMerge=true"));
    }

}
