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

/**
 * Common methods for working with strings.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 */
public class StringUtils
{

    private StringUtils()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Checks that the specified string is not {@code null}, empty ({@code ""}), or blank
     * (containing whitespace only). If the string is valid, returns a copy of it, trimmed;
     * otherwise, throws an {@link IllegalArgumentException}.
     * <p>
     * This method is designed primarily for doing parameter validation in methods and
     * constructors with multiple parameters, as demonstrated below: <blockquote>
     *
     * <pre>
     * public Foo(Bar bar)
     * {
     *     this.bar = StringUtils.requireNonBlankAndTrim(bar, "bar must not be blank");
     * }
     * </pre>
     *
     * </blockquote>
     *
     * @param string  the string to check
     * @param message detail message for the exception to be used if the string a blank
     *
     * @return {@code string}, trimmed, if not {@code null} or blank
     *
     * @throws IllegalArgumentException if {@code string} is {@code null} or blank
     * @since 1.0.0
     */
    public static String requireNonBlankAndTrim(String string, String message)
    {
        if (isBlank(string))
        {
            throw new IllegalArgumentException(message);
        }
        return string.trim();
    }

    /**
     * Checks if a {@code CharSequence} is empty ("") or null.
     *
     * @param charSequence the {@code CharSequence} to check, may be null
     * @return {@code true} if the {@code CharSequence} is empty or null
     * @since 1.1.0
     */
    public static boolean isEmpty(CharSequence charSequence)
    {
        return charSequence == null || charSequence.length() == 0;
    }

    /**
     * Checks if a {@code CharSequence} is empty (""), null or whitespace only.
     *
     * @param charSequence the {@code CharSequence} to check, may be null
     * @return {@code true} if the {@code CharSequence} is null, empty or whitespace only
     * @since 1.1.0
     */
    public static boolean isBlank(CharSequence charSequence)
    {
        if (!isEmpty(charSequence))
        {
            for (int i = 0; i < charSequence.length(); i++)
            {
                if (!Character.isWhitespace(charSequence.charAt(i)))
                {
                    return false;
                }
            }
        }
        return true;
    }

}
