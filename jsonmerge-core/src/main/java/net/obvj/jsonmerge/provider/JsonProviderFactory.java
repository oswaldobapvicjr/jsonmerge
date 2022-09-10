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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A factory that creates {@link JsonProvider} instances for specified JSON object types.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
public final class JsonProviderFactory
{

    private static final JsonProviderFactory INSTANCE = new JsonProviderFactory();

    // Associates each class name with the assignable JsonProvider
    private static final Map<String, Supplier<JsonProvider<?>>> PROVIDERS = new HashMap<>();

    static
    {
        PROVIDERS.put("net.minidev.json.JSONObject", JsonSmartJsonProvider::new);
        PROVIDERS.put("com.google.gson.JsonObject", GsonJsonProvider::new);
        PROVIDERS.put("com.fasterxml.jackson.databind.JsonNode", JacksonJsonNodeJsonProvider::new);
        PROVIDERS.put("com.fasterxml.jackson.databind.node.ObjectNode", JacksonJsonNodeJsonProvider::new);
        PROVIDERS.put("org.json.JSONObject", JsonOrgJsonProvider::new);
        PROVIDERS.put("io.vertx.core.json.JsonObject", VertxJsonProvider::new);
    }

    /**
     * Returns a single {@code JsonOrgJsonProvider} instance.
     *
     * @return a {@code JsonOrgJsonProvider} instance
     */
    public static JsonProviderFactory instance()
    {
        return INSTANCE;
    }

    private JsonProviderFactory()
    {
        // Instantiation not allowed
    }

    /**
     * Returns a {@link JsonProvider} instance for the specified JSON object class.
     *
     * @param <T>            the actual JSON object type
     * @param jsonObjectType the class that represents a JSON document on a supported
     *                       {@code JsonProvider}; not null
     *
     * @return a new {@link JsonProvider} instance; never null
     *
     * @throws NullPointerException     if the specified {@code jsonObjectType} is null
     * @throws IllegalArgumentException if no {@link JsonProvider} found for the specified
     *                                  {@code jsonObjectType}
     */
    public <T> JsonProvider<T> getByType(final Class<T> jsonObjectType)
    {
        Objects.requireNonNull(jsonObjectType, "The search type must not be null");
        String className = jsonObjectType.getCanonicalName();
        Supplier<JsonProvider<?>> supplier = PROVIDERS.get(className);
        if (supplier == null)
        {
            throw new IllegalArgumentException("No JsonProvider available for " + className);
        }
        return (JsonProvider<T>) supplier.get();
    }

}
