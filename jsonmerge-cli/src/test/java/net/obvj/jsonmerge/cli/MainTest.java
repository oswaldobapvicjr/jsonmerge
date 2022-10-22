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

package net.obvj.jsonmerge.cli;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import net.obvj.jsonmerge.JsonMergeOption;

/**
 * Unit tests for the {@link Main} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
class MainTest
{
    Main main = new Main();

    @Test
    void parseJsonMergeOption_oneKey_success()
    {
        assertThat(main.parseJsonMergeOption(Map.entry("path1", "key1")), equalTo(JsonMergeOption
                .onPath("path1").findObjectsIdentifiedBy("key1").thenDoADeepMerge()));
    }

    @Test
    void parseJsonMergeOption_twoKeys_success()
    {
        assertThat(main.parseJsonMergeOption(Map.entry("path1", "key1,key2")),
                equalTo(JsonMergeOption.onPath("path1").findObjectsIdentifiedBy("key1", "key2")
                        .thenDoADeepMerge()));
    }

}
