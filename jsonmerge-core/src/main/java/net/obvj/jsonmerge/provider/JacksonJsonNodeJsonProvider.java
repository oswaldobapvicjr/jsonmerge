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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A specialized {@link JsonProvider} implementation for {@code Jackson}'s
 * {@link JsonNode}.
 *
 * @author oswaldo.bapvic.jr (Oswaldo Junior)
 * @since 1.0.0
 *
 * @see <a href="https://github.com/FasterXML/jackson">Jackson Project home</a>
 * @see JsonNode
 * @see ObjectNode
 * @see ArrayNode
 */
public class JacksonJsonNodeJsonProvider implements JsonProvider
{
    private ObjectNode toJsonObject(final Object jsonObject)
    {
        return (ObjectNode) jsonObject;
    }

    private ArrayNode toJsonArray(final Object jsonArray)
    {
        return (ArrayNode) jsonArray;
    }

    /**
     * Converts an object to a {@link JsonNode}.
     *
     * @param object the object to be converted
     * @return a {@link JsonNode} from the specified object
     */
    public JsonNode toJsonNode(final Object object)
    {
        return object instanceof JsonNode ? (JsonNode) object
                : new ObjectMapper().convertValue(object, JsonNode.class);
    }

    @Override
    public boolean isJsonObject(final Object object)
    {
        return object instanceof ObjectNode;
    }

    @Override
    public boolean isJsonArray(final Object object)
    {
        return object instanceof ArrayNode;
    }

    @Override
    public boolean isEmpty(final Object jsonObject)
    {
        return toJsonObject(jsonObject).isEmpty();
    }

    @Override
    public Object newJsonObject()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    @Override
    public Object newJsonObject(final Object sourceJsonObject)
    {
        return JsonNodeFactory.instance.objectNode().setAll(toJsonObject(sourceJsonObject));
    }

    @Override
    public Object newJsonArray()
    {
        return JsonNodeFactory.instance.arrayNode();
    }

    @Override
    public Object newJsonArray(final Object sourceJsonArray)
    {
        return JsonNodeFactory.instance.arrayNode().addAll(toJsonArray(sourceJsonArray));
    }

    @Override
    public Set<Entry<String, Object>> entrySet(final Object jsonObject)
    {
        Iterator<Entry<String, JsonNode>> iterator = toJsonObject(jsonObject).fields();
        Iterable<Entry<String, JsonNode>> iterable = () -> iterator;
        return (Set) StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
    }

    @Override
    public Object get(final Object jsonObject, final String key)
    {
        return toJsonObject(jsonObject).get(key);
    }


    @Override
    public void put(final Object jsonObject, final String key, final Object value)
    {
        toJsonObject(jsonObject).set(key, toJsonNode(value));
    }

    @Override
    public void putIfAbsent(final Object jsonObject, final String key, final Object value)
    {
        ObjectNode json = toJsonObject(jsonObject);
        if (json.get(key) == null)
        {
            json.set(key, toJsonNode(value));
        }
    }

    @Override
    public void add(final Object jsonArray, final Object element)
    {
        toJsonArray(jsonArray).add(toJsonNode(element));
    }

    @Override
    public void set(Object jsonArray, int index, Object element)
    {
        toJsonArray(jsonArray).set(index, toJsonNode(element));
    }

    @Override
    public int indexOf(Object jsonArray, Object element)
    {
        ArrayNode array = toJsonArray(jsonArray);
        return IntStream.range(0, array.size())
                .filter(index -> Objects.equals(array.get(index), element))
                .findFirst().orElse(-1);
    }

    @Override
    public void forEachElementInArray(final Object jsonArray, final Consumer<? super Object> action)
    {
        toJsonArray(jsonArray).forEach(action);
    }

    @Override
    public boolean arrayContains(final Object jsonArray, final Object element)
    {
        return stream(jsonArray).anyMatch(element::equals);
    }

    @Override
    public Stream<Object> stream(final Object jsonArray)
    {
        Spliterator<JsonNode> spliterator = toJsonArray(jsonArray).spliterator();
        return (Stream) StreamSupport.stream(spliterator, false);
    }

}
