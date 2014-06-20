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
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugin.CheckBySsh;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Massimiliano Ziccardi
 */

@Test
public class CheckBySshIT implements ITConstants {

    /**
     * Username to be used to access the ssh server.
     */
    private String sshUsername;
    /**
     * Password to be used to access the ssh server.
     */
    private String sshPassword;
    
    /**
     * <code>true</code> if SSH test should be performed.
     */
    private boolean performSSHTests;

    /**
     * Initializes username and password and enables/disables the SSH tests
     * according to the found properties.
     * 
     * @throws Exception on any errror
     */
    @BeforeClass
    public final void setup() throws Exception {
        if (ITSetup.getPluginRepository() == null) {
            ITSetup.setUp();
        }

        if (System.getProperty("testSSH") != null) {
            performSSHTests = Boolean.getBoolean("testSSH");
        } else {
            performSSHTests = false;
        }

        sshUsername = System.getProperty("ssh.username");
        sshPassword = System.getProperty("ssh.password");

        PluginDefinition checkProcs = PluginRepositoryUtil.loadFromPluginAnnotation(CheckBySsh.class);
        ITSetup.getPluginRepository().addPluginDefinition(checkProcs);
    }

    /**
     * Invokes the check_ssh plugin through the {@link JNRPEClient} object and check that
     * the program executions works.
     * 
     * @throws JNRPEClientException on any JNRPE error
     */
    @Test
    public final void checkBySSH() throws JNRPEClientException {

        if (!performSSHTests || sshUsername == null || sshPassword == null) {
            System.out.println("SSH test will be skipped. To test it, install an SSH server and " + "run again the tests passing :\n"
                    + "-Dssh.username={YOUR SSH USERNAME}\n" + "-Dssh.password={YOUR_SSH_PASSWORD}\n" + "-DtestSSH=true");
            return;
        }

        CommandRepository cr = ITSetup.getCommandRepository();

        CommandDefinition cd = new CommandDefinition("CHECK_BY_SSH_TEST", "CHECK_BY_SSH");
        cd.addArgument(new CommandOption("hostname", "$ARG1$"));
        cd.addArgument(new CommandOption("port", "$ARG2$"));
        cd.addArgument(new CommandOption("username", "$ARG3$"));
        cd.addArgument(new CommandOption("password", "$ARG4$"));
        cd.addArgument(new CommandOption("command", "$ARG5$"));
        cr.addCommandDefinition(cd);

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_BY_SSH_TEST", "127.0.0.1", "22", sshUsername, sshPassword, "ls /tmp");

        Assert.assertEquals(ret.getStatus(), Status.OK);

        // TODO : should check that the output of the command is ok...
    }

}
