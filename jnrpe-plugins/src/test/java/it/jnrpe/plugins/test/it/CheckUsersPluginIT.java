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
package it.jnrpe.plugins.test.it;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugin.CheckUsers;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Unit test for the check_users plugin
 *
 * @author Frederico Campos
 *
 */
public class CheckUsersPluginIT implements ITConstants {

    @BeforeTest
    public void setup() throws Exception {
        PluginDefinition checkUsers =
                PluginRepositoryUtil.loadFromPluginAnnotation(CheckUsers.class);

        ITSetup.getPluginRepository().addPluginDefinition(checkUsers);
    }

    @Test
    public void checkUsersOk() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();
        cr.addCommandDefinition(new CommandDefinition("CHECK_USERS",
                "CHECK_USERS").addArgument(new CommandOption("w", "$ARG1$"))
                .addArgument(new CommandOption("c", "$ARG2$")));
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_USERS", "10:", "20:");
        Assert.assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void checkUsersWarning() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();
        cr.addCommandDefinition(new CommandDefinition("CHECK_USERS",
                "CHECK_USERS").addArgument(new CommandOption("w", "$ARG1$"))
                .addArgument(new CommandOption("c", "$ARG2$")));
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_USERS", "0:", "20:");
        Assert.assertEquals(ret.getStatus(), Status.WARNING);
    }

    @Test
    public void checkUsersCritical() throws Exception {
        //System.out.println("Running checkUsers");
        CommandRepository cr = ITSetup.getCommandRepository();
        cr.addCommandDefinition(new CommandDefinition("CHECK_USERS",
                "CHECK_USERS").addArgument(new CommandOption("w", "$ARG1$"))
                .addArgument(new CommandOption("c", "$ARG2$")));
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_USERS", "~:0", "0:");
        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
    }
}
