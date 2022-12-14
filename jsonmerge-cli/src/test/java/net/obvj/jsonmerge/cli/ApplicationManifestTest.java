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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static net.obvj.jsonmerge.cli.ApplicationManifest.loadManifest;
import static net.obvj.jsonmerge.cli.ApplicationManifest.parseCommandName;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ApplicationManifest} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
class ApplicationManifestTest
{

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(ApplicationManifest.class, instantiationNotAllowed());
    }

    @Test
    void parseCommandName_validManifest_dynamicString()
    {
        assertThat(parseCommandName(loadManifest("manifest1.mf")),
                equalTo("jsonmerge-cli-1.0.0.jar"));
    }

    @Test
    void parseCommandName_fileNotFound_defaultString()
    {
        assertThat(parseCommandName(loadManifest("notfound.mf")),
                equalTo("jsonmerge-cli-<version>.jar"));
    }

}
