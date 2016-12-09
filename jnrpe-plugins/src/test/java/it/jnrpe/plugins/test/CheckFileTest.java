/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
 *******************************************************************************/
package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.plugin.CCheckFile;
import org.junit.Test;

import java.io.File;

/**
 * Tests the check file plugin.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class CheckFileTest {
    /**
     * Path to the test file.
     */
    private final static File TESTFILE = new File(
        "src/test/resources/check_file/testfile.txt");

    @Test
    public void checkFileExists() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', TESTFILE.getAbsolutePath())
            .expect(Status.OK);

    }

    @Test
    public void checkFileExistsCritical() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', "non_existent_file")
            .expect(Status.CRITICAL);

    }

    @Test
    public void checkFileNotExists() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("FILE", 'F', "non_existent_file")
            .expect(Status.OK);

    }

    @Test
    public void checkFileNotExistsCritical() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("FILE", 'F', TESTFILE.getAbsolutePath())
            .expect(Status.CRITICAL);

    }

    @Test
    public void checkFileNotContainsOk() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', TESTFILE.getAbsolutePath())
            .withOption("notcontains", 'N', "notexistentstring")
            .expect(Status.OK);

    }

    @Test
    public void checkFileNotContainsCritical() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', TESTFILE.getAbsolutePath())
            .withOption("notcontains", 'N', "verso")
            .expect(Status.CRITICAL);

    }

    @Test
    public void checkFileContainsOk() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', TESTFILE.getAbsolutePath())
            .withOption("contains", 'O', "verso,0:2,2:5")
            .expect(Status.OK);

    }

    @Test
    public void checkFileContainsWarning() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', TESTFILE.getAbsolutePath())
            .withOption("contains", 'O', "verso,2:,0:2")
            .expect(Status.WARNING);

    }

    @Test
    public void checkFileContainsCritical() throws Exception {

        PluginTester.given(new CCheckFile())
            .withOption("file", 'f', TESTFILE.getAbsolutePath())
            .withOption("contains", 'O', "verso,2:4,4:")
            .expect(Status.CRITICAL);

    }
}