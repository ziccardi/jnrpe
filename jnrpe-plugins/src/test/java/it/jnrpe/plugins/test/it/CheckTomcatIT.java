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
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for the checktomcat plugin.
 *
 * @author Massimiliano Ziccardi
 */
@Test
public class CheckTomcatIT implements ITConstants {

    /**
     * The port where tomcat listens.
     */
    private static final String TOMCAT_PORT = "7070";

    /**
     * running single unit test.
     */
    private boolean single = false;


    @BeforeClass
    public final void setup() {
        try {
            if (ITSetup.getPluginRepository() == null){
                ITSetup.setUp();
                this.single = true;
            }

            ClassLoader cl = CheckTomcatIT.class.getClassLoader();

            PluginDefinition checkTomcat =
                    PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                            cl.getResourceAsStream("check_tomcat_plugin.xml"));

            ITSetup.getPluginRepository().addPluginDefinition(checkTomcat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Executes the test.
     * @throws Exception -
     */
    @Test
    public final void checkTomcatOK() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_TOMCAT",
                "CHECK_TOMCAT")
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_TOMCAT",
                        "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat");

        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    /**
     * check if 1 or more of threads are available, assert status is warning
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsWarning() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_WARN", "CHECK_TOMCAT")
                .addArgument(new CommandOption("threads"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_THREAD_WARN", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", "1:");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }


    /**
     * check 1 or more threads are available, assert status is critical
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsCritical() throws Exception {
        try{
            CommandRepository cr = ITSetup.getCommandRepository();

            cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_CRIT", "CHECK_TOMCAT")
                    .addArgument(new CommandOption("threads"))
                    .addArgument(new CommandOption("hostname", "$ARG1$"))
                    .addArgument(new CommandOption("port", "$ARG2$"))
                    .addArgument(new CommandOption("username", "$ARG3$"))
                    .addArgument(new CommandOption("password", "$ARG4$"))
                    .addArgument(new CommandOption("critical", "$ARG5$"))
                    );

            JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            ReturnValue ret = client.sendCommand("CHECK_THREAD_CRIT", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", "1:");

            Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check if 99.9%  or less of maximum threads are available, assert status is warning
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsWarningPercentage() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_PERC_WARN", "CHECK_TOMCAT")
                .addArgument(new CommandOption("threads"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                .addArgument(new CommandOption("percent"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_THREAD_PERC_WARN", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    /**
     * check if 99.9% or less of maximum threads are available, assert status is critical
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsCriticalPercentage() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_PERC_CRIT", "CHECK_TOMCAT")
                .addArgument(new CommandOption("threads"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("critical", "$ARG5$"))
                .addArgument(new CommandOption("percent"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_THREAD_PERC_CRIT", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }


    public final void checkTomcatMemoryWarningPercentage() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MEM_PERC_WARNING", "CHECK_TOMCAT")
                .addArgument(new CommandOption("memory"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                .addArgument(new CommandOption("percent"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_MEM_PERC_WARNING", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    /**
     * check if less than 99.9% of maximum memory is avaliable, assert status is critical
     * @throws Exception
     * void
     */
    public final void checkTomcatMemoryCriticalPercentage() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MEM_PERC_WARNING", "CHECK_TOMCAT")
                .addArgument(new CommandOption("memory"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("critical", "$ARG5$"))
                .addArgument(new CommandOption("percent"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_MEM_PERC_WARNING", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }
    /**
     * Check 50% or less of memory is available
     * @throws Exception
     * void
     */
    public final void checkTomcatMemoryOKPercentage() throws Exception {
        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MEM_PERC_OK", "CHECK_TOMCAT")
                .addArgument(new CommandOption("memory"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("critical", "$ARG5$"))
                .addArgument(new CommandOption("percent"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_MEM_PERC_OK", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":50");

        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }


    /**
     * Stop tomcat.
     */
    @AfterClass
    public final void tearDown() throws Exception {
        if (single){
            ITSetup.shutDown();
        }
    }

}
