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

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * A class that holds application information retrieved from the {@code MANIFEST.MF} file.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
public class ApplicationManifest
{
    /**
     * The application name; a string in the format {@code <artifactId>-<version>.jar}
     */
    public static final String COMMAND_NAME = parseCommandName(loadManifest());

    private static Manifest loadManifest()
    {
        return loadManifest("META-INF/MANIFEST.MF");
    }

    /**
     * Loads a {@link Manifest} from the specified file name, or an "empty" object if the
     * specified file name is not found or not accessible.
     *
     * @param fileName the manifest file name to be loaded
     * @return a {@link Manifest} from the specified file name
     */
    static Manifest loadManifest(String fileName)
    {
        try (InputStream input = ApplicationManifest.class.getClassLoader()
                .getResourceAsStream(fileName))
        {
            return new Manifest(input);
        }
        catch (Exception exception)
        {
            return new Manifest();
        }
    }

    static String parseCommandName(Manifest manifest)
    {
        return String.format("jsonmerge-cli-%s.jar",
                defaultIfNull(manifest.getMainAttributes(), "AppVersion", "<version>"));
    }

    private static String defaultIfNull(Attributes attributes, String key, String defaultValue)
    {
        String value = attributes.getValue(key);
        return value != null ? value : defaultValue;
    }

    private ApplicationManifest()
    {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

}
