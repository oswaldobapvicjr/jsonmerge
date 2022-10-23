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

import static net.obvj.jsonmerge.JsonMergeOption.onPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.obvj.jsonmerge.JsonMergeOption;
import picocli.CommandLine;

/**
 * Unit tests for the {@link Main} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.0
 */
class MainTest
{
    private static final String TARGET_FILE = "target/result.json";

    // Program arguments
    private static final String ARG_FILE1 = "src/test/resources/file1.json";
    private static final String ARG_FILE2 = "src/test/resources/file2.json";
    private static final String ARG_D_PATH1_KEY1 = "-dpath1=key1";
    private static final String ARG_D_PATH1_KEY1_KEY2 = "-dpath1=key1,key2";
    private static final String ARG_D_PATH2_KEY3 = "-dpath2=key3";
    private static final String ARG_PRETTY = "-p";
    private static final String ARG_TARGET_FILE = "-t " + TARGET_FILE;


    // Expected objects
    private static final JsonMergeOption MERGE_OPTION_PATH1_KEY1 = onPath("path1")
            .findObjectsIdentifiedBy("key1").thenDoADeepMerge();
    private static final JsonMergeOption MERGE_OPTION_PATH1_KEY1_KEY2 = onPath("path1")
            .findObjectsIdentifiedBy("key1", "key2").thenDoADeepMerge();
    private static final JsonMergeOption MERGE_OPTION_PATH2_KEY3 = onPath("path2")
            .findObjectsIdentifiedBy("key3").thenDoADeepMerge();
    private static final String MERGE_JSON1_JSON2_COMPACT = "{\"keyA\":\"valueA\",\"keyB\":\"valueB\"}";
    private static final List<String> MERGE_JSON1_JSON2_PRETTY = Arrays.asList("{",
            "  \"keyA\": \"valueA\",", "  \"keyB\": \"valueB\"", "}");


    // Test subject
    private Main main = new Main();
    private CommandLine commandLine = new CommandLine(main);

    @Test
    void parseJsonMergeOptions_oneKey_success()
    {
        commandLine.parseArgs(ARG_D_PATH1_KEY1, ARG_FILE1, ARG_FILE2);
        assertThat(main.parseJsonMergeOptions(),
                equalTo(new JsonMergeOption[] { MERGE_OPTION_PATH1_KEY1 }));
    }

    @Test
    void parseJsonMergeOptions_twoKeys_success()
    {
        commandLine.parseArgs(ARG_D_PATH1_KEY1_KEY2, ARG_FILE1, ARG_FILE2);
        assertThat(main.parseJsonMergeOptions(),
                equalTo(new JsonMergeOption[] { MERGE_OPTION_PATH1_KEY1_KEY2 }));
    }

    @Test
    void parseJsonMergeOptions_twoArguments_success()
    {
        commandLine.parseArgs(ARG_D_PATH1_KEY1_KEY2, ARG_D_PATH2_KEY3, ARG_FILE1, ARG_FILE2);
        assertThat(main.parseJsonMergeOptions(), equalTo(
                new JsonMergeOption[] { MERGE_OPTION_PATH1_KEY1_KEY2, MERGE_OPTION_PATH2_KEY3 }));
    }

    @Test
    void parseAndMerge_validFiles_compactResult() throws IOException
    {
        commandLine.parseArgs(ARG_FILE1, ARG_FILE2);
        assertThat(main.parseAndMerge(), equalTo(MERGE_JSON1_JSON2_COMPACT));
    }

    @Test
    void parseAndMerge_validFilesAndPrettyOption_prettyResult() throws IOException
    {
        commandLine.parseArgs(ARG_PRETTY, ARG_FILE1, ARG_FILE2);
        assertAllLines(MERGE_JSON1_JSON2_PRETTY, main.parseAndMerge());
    }

    private static void assertAllLines(List<String> expectedLines, String actual)
    {
        String[] actualLines = actual.split("\n");
        for (int i = 0; i < actualLines.length; i++)
        {
            assertThat(actualLines[i], equalTo(expectedLines.get(i)));
        }
    }

    @Test
    void execute_fileNotFound_errorCode()
    {
        assertThat(commandLine.execute("nonExistingFile.json", ARG_FILE2), equalTo(1));
    }

    @Test
    void execute_validFiles_targetFileGeneratedSuccessfully() throws IOException
    {
        assertThat(commandLine.execute(ARG_TARGET_FILE, ARG_FILE1, ARG_FILE2), equalTo(0));
        assertThat(Files.readAllBytes(Path.of(TARGET_FILE)),
                equalTo(MERGE_JSON1_JSON2_COMPACT.getBytes()));
    }

}
