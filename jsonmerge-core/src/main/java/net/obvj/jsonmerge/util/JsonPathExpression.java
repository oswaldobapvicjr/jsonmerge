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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;

/**
 * An immutable object that represents a compiled {@code JsonPath} expression.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0.0
 */
public class JsonPathExpression
{

    /**
     * A pre-compiled {@code JsonPathExpression} that represents the root element of a JSON
     * document ('{@code $}').
     */
    public static final JsonPathExpression ROOT = new JsonPathExpression("$");

    private static final String CHILD_TO_EXPRESSION_PATTERN = "['%s']";

    private final JsonPath jsonPath;

    /**
     * Creates a compiled {@code JsonPathExpression} from the specified expression.
     *
     * @param expression the JsonPath expression to compile
     * @throws IllegalArgumentException if the specified expression is null or empty
     * @throws InvalidPathException     if the specified JsonPath expression is invalid
     */
    public JsonPathExpression(final String expression)
    {
        if (StringUtils.isEmpty(expression))
        {
            throw new IllegalArgumentException("The JsonPath expression can not be null or empty");
        }
        jsonPath = JsonPath.compile(expression);
    }

    /**
     * Produces a new {@code JsonPathExpression} with the concatenation result of this
     * {@code JsonPathExpression} and the given child element name.
     *
     * @param name the child name to be appended to the end of this {@code JsonPathExpression}
     * @return a new, compiled {@link JsonPathExpression}
     *
     * @throws InvalidPathException if the resulting JsonPath expression is invalid
     */
    public JsonPathExpression appendChild(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            return this;
        }
        String expression = String.format(CHILD_TO_EXPRESSION_PATTERN, name);
        return append(expression);
    }

    /**
     * Produces a new {@code JsonPathExpression} with the concatenation result of this
     * {@code JsonPathExpression} and the given expression.
     *
     * @param expression a {@code String} representing the expression to be appended to the
     *                   end of this {@code JsonPathExpression}
     * @return a new, compiled {@link JsonPathExpression}
     *
     * @throws InvalidPathException if the resulting JsonPath expression is invalid
     */
    public JsonPathExpression append(final String expression)
    {
        if (StringUtils.isEmpty(expression))
        {
            return this;
        }
        String currentPath = toString();
        return new JsonPathExpression(currentPath + expression);
    }

    /**
     * Returns the string representation of this {@code JsonPathExpression}, in the
     * bracket-notation. For example: {@code $['store']['book'][0]['title']}
     */
    @Override
    public String toString()
    {
        return jsonPath.getPath();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(jsonPath.getPath());
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (getClass() != other.getClass())
        {
            return false;
        }
        return Objects.equals(jsonPath.getPath(), ((JsonPathExpression) other).jsonPath.getPath());
    }

}
