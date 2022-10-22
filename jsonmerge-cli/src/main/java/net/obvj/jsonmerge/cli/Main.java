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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.obvj.jsonmerge.JsonMergeOption;
import net.obvj.jsonmerge.JsonMerger;
import net.obvj.jsonmerge.provider.JsonProvider;
import net.obvj.jsonmerge.provider.JsonProviderFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * A command line tool to merge JSON files.
 * <p>
 * Usage:
 *
 * <pre>
 * {@code Main [-hp] -t <result.json> [-d <path=key>]... <FILE1> <FILE2>}
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
@Command(name = "jsonmerge-cli-1.2.0.jar",
         separator = " ",
         usageHelpWidth = 85,
         parameterListHeading = "\nParameters:\n\n",
         optionListHeading = "\nOptions:\n\n")
public class Main implements Callable<Integer>
{

    @Parameters(index = "0",
            paramLabel = "<FILE1>",
            description = "The first file to merge")
    private Path file1;

    @Parameters(index = "1",
            paramLabel = "<FILE2>",
            description = "The second file to merge")
    private Path file2;

    @Option(names = { "-t", "--target" },
            description = "The target file name (default: result.json)",
            defaultValue = "result.json",
            required = true)
    private Path target;

    @Option(names = { "-d", "--distinct" },
            paramLabel = "<exp=key>",
            description = { "Defines one or more distinct keys inside a child path",
                    "For example: -d $.agents=name",
                    "             -d $.files=id,version" })
    private Map<String, String> distinctKeys = Collections.emptyMap();

    @Option(names = { "-p", "--pretty" },
            description = "Generates a well-formatted result file")
    private boolean prettyPrinting = false;

    @Option(names = { "-h", "--help" },
            usageHelp = true,
            description = "Displays a help message")
    private boolean helpRequested = false;

    private JsonProvider<JsonObject> provider = JsonProviderFactory.instance()
            .getByType(JsonObject.class);

    private GsonBuilder gsonBuilder = new GsonBuilder();

    private Logger log = LoggerFactory.getLogger(Main.class);

    @Override
    public Integer call() throws Exception
    {
        String result = parseAndMerge();

        log.info("Generating output file {} ...", target);
        Files.write(target, result.getBytes());

        log.info("Success");
        return 0;
    }

    String parseAndMerge() throws IOException
    {
        if (prettyPrinting) gsonBuilder.setPrettyPrinting();

        log.info("Parsing {} ...", file1);
        JsonObject json1 = provider.parse(Files.newInputStream(file1));

        log.info("Parsing {} ...", file2);
        JsonObject json2 = provider.parse(Files.newInputStream(file2));

        JsonMergeOption[] mergeOptions = parseJsonMergeOptions();
        JsonObject result = new JsonMerger<>(JsonObject.class)
                .merge(json1, json2, mergeOptions);

        return toString(result);
    }

    private JsonMergeOption[] parseJsonMergeOptions()
    {
        return distinctKeys.entrySet().stream()
                .map(this::parseJsonMergeOption)
                .toArray(JsonMergeOption[]::new);
    }

    JsonMergeOption parseJsonMergeOption(Entry<String, String> entry)
    {
        JsonMergeOption mergeOption = JsonMergeOption.onPath(entry.getKey())
                .findObjectsIdentifiedBy(split(entry.getValue()))
                .thenDoADeepMerge();
        log.info("{}", mergeOption);
        return mergeOption;
    }

    private String[] split(String string)
    {
        return string.split(",");
    }

    String toString(JsonObject json)
    {
        return gsonBuilder.create().toJson(json);
    }

    public static void main(String[] args)
    {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

}
