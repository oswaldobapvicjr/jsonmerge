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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.InvalidPathException;

/**
 * Unit tests for the {@link JsonPathExpression} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
class JsonPathExpressionTest
{

    @Test
    void root_rootExpression()
    {
        assertEquals("$", JsonPathExpression.ROOT.toString());
    }

    @Test
    void constructor_validBracketNotationExpression_success()
    {
        assertEquals("$['title']", new JsonPathExpression("$['title']").toString());
    }

    @Test
    void constructor_validDotNotationExpression_success()
    {
        assertEquals("$['name']", new JsonPathExpression("$.name").toString());
    }

    @Test
    void constructor_null_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> new JsonPathExpression(null));
    }

    @Test
    void constructor_invalidExpression_exception()
    {
        assertThrows(InvalidPathException.class, () -> new JsonPathExpression("$."));
    }

    @Test
    void appendKey_additionalFieldOnRoot_success()
    {
        assertEquals("$['agents']", new JsonPathExpression("$").appendChild("agents").toString());
    }

    @Test
    void append_additionalField_success()
    {
        assertEquals("$['agent']['class']", new JsonPathExpression("$.agent").append("class").toString());
    }

    @Test
    void append_additionalExpression_success()
    {
        assertEquals("$['agent']['class']", new JsonPathExpression("$.agent").append("['class']").toString());
    }

    @Test
    void append_elementIndex_success()
    {
        assertEquals("$['agents'][0]", new JsonPathExpression("$.agents").append("[0]").toString());
    }

    @Test
    void appendKey_emptyValue_sameExpression()
    {
        JsonPathExpression expression = new JsonPathExpression("$.agent.class");
        assertSame(expression, expression.appendChild(""));
    }

    @Test
    void append_emptyValue_sameExpression()
    {
        JsonPathExpression expression = new JsonPathExpression("$.agent.class");
        assertSame(expression, expression.append(""));
    }

    @Test
    void cleanup_success()
    {
        assertEquals(new JsonPathExpression("$.foo[*].bar[*].baz"),
                new JsonPathExpression("$.foo[0].bar[1].baz").cleanUp());

        JsonPathExpression unchangeableExpression = new JsonPathExpression("$.foo.bar.baz");
        assertEquals(unchangeableExpression, unchangeableExpression.cleanUp());
    }

    @Test
    void equals_similarObjects_true()
    {
        assertEquals(new JsonPathExpression("$['attr']"), new JsonPathExpression("$.attr"));
    }

    @Test
    void equals_sameObject_true()
    {
        JsonPathExpression expression = new JsonPathExpression("$['attr']");
        assertEquals(expression, expression);
    }

    @Test
    void equals_null_false()
    {
        assertFalse(new JsonPathExpression("$['attr']").equals(null));
    }

    @Test
    void equals_differentObjects_false()
    {
        JsonPathExpression expression = new JsonPathExpression("$['attr']");
        assertFalse(expression.equals(new Object()));
        assertFalse(expression.equals(new JsonPathExpression("$")));
    }

    @Test
    void equals_similarObjectsInAHashSet_noRepeatedElements()
    {
        List<JsonPathExpression> list = Arrays.asList(new JsonPathExpression("$.key"),
                new JsonPathExpression("$['key']"));
        Set<JsonPathExpression> set = new HashSet<>(list);

        assertEquals(1, set.size());
        assertTrue(set.containsAll(list));
    }

}
