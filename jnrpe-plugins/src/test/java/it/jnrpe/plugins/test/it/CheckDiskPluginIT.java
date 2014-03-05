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
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CheckDiskPluginIT implements ITConstants {

    private final static File m_testPath = new File(
            "/");

    @BeforeTest
    public void setup() throws Exception {
        ClassLoader cl = CheckDiskPluginIT.class.getClassLoader();

        PluginDefinition checkDist =
                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                        cl.getResourceAsStream("check_disk_plugin.xml"));

        ITSetup.getPluginRepository().addPluginDefinition(checkDist);

        CommandRepository cr = ITSetup.getCommandRepository();
        cr.addCommandDefinition(
                new CommandDefinition("CHECK_DISK",
                        "CHECK_DISK")
                        .addArgument(new CommandOption("path", "$ARG1$"))
                        .addArgument(new CommandOption("warning", "$ARG2$"))
                        .addArgument(new CommandOption("critical", "$ARG3$"))
                );

        cr.addCommandDefinition(
                new CommandDefinition("CHECK_DISK_NEW",
                        "CHECK_DISK")
                        .addArgument(new CommandOption("path", "$ARG1$"))
                        .addArgument(
                                new CommandOption("th",
                                        "metric=freepct,ok=$ARG2$,warn=$ARG3$,crit=$ARG4$,unit=%"))
                );
    }

    @Test(enabled=false)
    public void checkDiskOldThresholdsOK() throws Exception {

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_DISK",
                        m_testPath.getAbsolutePath(),
                        "5:10",
                        ":5");

        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test(enabled=false)
    public void checkDiskNewThresholdsPctOK() throws Exception {

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_DISK_NEW",
                        m_testPath.getAbsolutePath(),
                        "15..inf",
                        "10..15",
                        "inf..10");

        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test(enabled=false)
    public void checkDiskOldThresholdsWarning() throws Exception {

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_DISK",
                        m_testPath.getAbsolutePath(),
                        "5:30",
                        ":5");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test(enabled=false)
    public void checkDiskNewThresholdsPctWarning() throws Exception {

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_DISK_NEW",
                        m_testPath.getAbsolutePath(),
                        "30.1..inf",
                        "5..30",
                        "inf..5");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test(enabled=false)
    public void checkDiskOldThresholdsCritical() throws Exception {

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_DISK",
                        m_testPath.getAbsolutePath(),
                        "30:",
                        ":30");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

    @Test(enabled=false)
    public void checkDiskNewThresholdsPctCritical() throws Exception {

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_DISK_NEW",
                        m_testPath.getAbsolutePath(),
                        "50.1..inf",
                        "30..50",
                        "inf..30");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }
}
