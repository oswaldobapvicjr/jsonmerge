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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import net.obvj.jsonmerge.util.JsonParseException;

/**
 * Provides common methods for {@link JsonProvider} implementations.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.1.0
 */
public abstract class AbstractJsonProvider<T> implements JsonProvider<T>
{

    @Override
    public T parse(String string)
    {
        Objects.requireNonNull(string, "The string to parse must not be null");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        return parse(inputStream);
    }

    @Override
    public T parse(InputStream inputStream)
    {
        try
        {
            return doParse(inputStream);
        }
        catch (Exception exception)
        {
            throw new JsonParseException(exception);
        }
    }

    /**
     * [Internal method] Deserializes a JSON from a specified {@code InputStream}.
     *
     * @param inputStream the source input stream
     * @return a JSON object of the type defined by this provider
     * @throws Exception in case of issues raised by the actual provider during parsing of the
     *                   input stream
     */
    abstract T doParse(InputStream inputStream) throws Exception;

}