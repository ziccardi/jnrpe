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
package it.jnrpe.plugins.test;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.plugin.test.CTestPlugin;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestPluginTest implements Constants {

    public TestPluginTest() {

    }

    @BeforeTest
    public void setup() throws Exception {
        PluginDefinition pd = 
                PluginRepositoryUtil.
                    loadFromPluginAnnotation(CTestPlugin.class);

        SetupTest.getPluginRepository().addPluginDefinition(pd);

        CommandDefinition cd =
                new CommandDefinition("TESTCOMMAND", "TEST").addArgument(
                        new CommandOption("text", "$ARG1$")).addArgument(
                        new CommandOption("status", "$ARG2$"));
        SetupTest.getCommandRepository().addCommandDefinition(cd);
    }

    @Test
    public void testStateCritical() throws Exception {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("TESTCOMMAND", "Hello World", "critical");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
        Assert.assertEquals(ret.getMessage(), "TEST : Hello World");
    }

    @Test
    public void testStateWarning() throws Exception {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("TESTCOMMAND", "Hello World!", "warning");

        Assert.assertEquals(ret.getStatus(), Status.WARNING);
        Assert.assertEquals(ret.getMessage(), "TEST : Hello World!");
    }

    @Test
    public void testStateOk() throws Exception {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("TESTCOMMAND", "Hello World!!!", "ok");

        Assert.assertEquals(ret.getStatus(), Status.OK);
        Assert.assertEquals(ret.getMessage(), "TEST : Hello World!!!");
    }

    @Test
    public void testStateUnknown() throws Exception {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("TESTCOMMAND", "Hello World!!!!!", "unknown");

        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
        Assert.assertEquals(ret.getMessage(), "TEST : Hello World!!!!!");
    }
}
