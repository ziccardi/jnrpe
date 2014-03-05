/*
 * Copyright (c) 2008 Massimiliano Ziccardi
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
package it.jnrpe.plugins.test.it;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugin.CCheckFile;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CheckFilePluginIT implements ITConstants {

    private final static File m_testFile = new File(
            "src/test/resources/check_file/testfile.txt");

    @BeforeTest
    public void setup() throws Exception {
        //ClassLoader cl = CheckFilePluginTest.class.getClassLoader();

        PluginDefinition checkFile = 
                PluginRepositoryUtil.loadFromPluginAnnotation(CCheckFile.class);
        
//        PluginDefinition checkFile =
//                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
//                        cl.getResourceAsStream("check_file_plugin.xml"));

        ITSetup.getPluginRepository().addPluginDefinition(checkFile);
    }

    @Test
    public void checkFileExists() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_EXISTS",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_EXISTS",
                        m_testFile.getAbsolutePath());

        Assert.assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void checkFileExistsCritical() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_EXISTS",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_FILE_EXISTS", "pippo");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
    }

    @Test
    public void checkFileNotExists() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_NOT_EXISTS",
                "CHECK_FILE").addArgument(new CommandOption("FILE", "$ARG1$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_FILE_NOT_EXISTS", "PIPPO");

        Assert.assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void checkFileNotExistsCritical() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_NOT_EXISTS",
                "CHECK_FILE").addArgument(new CommandOption("FILE", "$ARG1$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_NOT_EXISTS",
                        m_testFile.getAbsolutePath());

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
    }

    @Test
    public void checkFileNotContainsOk() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_NOT_CONTAIN",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$"))
                .addArgument(new CommandOption("notcontains", "$ARG2$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_NOT_CONTAIN",
                        m_testFile.getAbsolutePath(), "notexistentstring");

        Assert.assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void checkFileNotContainsCritical() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_NOT_CONTAIN",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$"))
                .addArgument(new CommandOption("notcontains", "$ARG2$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_NOT_CONTAIN",
                        m_testFile.getAbsolutePath(), "verso");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
    }

    @Test
    public void checkFileContainsOk() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_CONTAIN",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$"))
                .addArgument(new CommandOption("contains", "$ARG2$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_CONTAIN",
                        m_testFile.getAbsolutePath(), "verso,0:2,2:5");

        Assert.assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void checkFileContainsWarning() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_CONTAIN",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$"))
                .addArgument(new CommandOption("contains", "$ARG2$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_CONTAIN",
                        m_testFile.getAbsolutePath(), "verso,2:,0:2");

        Assert.assertEquals(ret.getStatus(), Status.WARNING);
    }

    @Test
    public void checkFileContainsCritical() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_FILE_CONTAIN",
                "CHECK_FILE").addArgument(new CommandOption("file", "$ARG1$"))
                .addArgument(new CommandOption("contains", "$ARG2$")));

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_FILE_CONTAIN",
                        m_testFile.getAbsolutePath(), "verso,2:4,4:");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
    }
}
