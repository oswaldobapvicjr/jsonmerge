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

package net.obvj.jsonmerge.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link StringUtils} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class StringUtilsTest
{
    private static final String TEST = "test";
    private static final String MESSAGE = "message";

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(StringUtils.class, instantiationNotAllowed());
    }

    @Test
    void requireNonBlank_validString_validString()
    {
        assertThat(StringUtils.requireNonBlankAndTrim(TEST, MESSAGE), equalTo(TEST));
    }

    @Test
    void requireNonBlank_validStringContainingBlankCharacter_trimmed()
    {
        assertThat(StringUtils.requireNonBlankAndTrim(" string ", MESSAGE), equalTo("string"));
    }

    @Test
    void requireNonBlank_null_illegalArgumentException()
    {
        assertThat(() -> StringUtils.requireNonBlankAndTrim(null, MESSAGE),
                throwsException(IllegalArgumentException.class).withMessage(MESSAGE));
    }

    @Test
    void requireNonBlank_empty_illegalArgumentException()
    {
        assertThat(() -> StringUtils.requireNonBlankAndTrim("", MESSAGE),
                throwsException(IllegalArgumentException.class).withMessage(MESSAGE));
    }

    @Test
    void requireNonBlank_blank_illegalArgumentException()
    {
        assertThat(() -> StringUtils.requireNonBlankAndTrim("\t", MESSAGE),
                throwsException(IllegalArgumentException.class).withMessage(MESSAGE));
    }
}
