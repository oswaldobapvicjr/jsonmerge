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
 * An exception raised in case of issues during parsing of a JSON string.
 * <p>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class JsonParseException extends RuntimeException
{

    private static final long serialVersionUID = -1829487003406780276L;

    /**
     * Creates a {@code JsonPathExpression} with the specified cause.
     *
     * @param cause the root exception that caused this exception to be thrown.
     */
    public JsonParseException(Throwable cause)
    {
        super(cause);
    }
}
