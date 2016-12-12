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
package it.jnrpe.test;

import it.jnrpe.JNRPE;
import it.jnrpe.JNRPEBuilder;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.events.LogEvent;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginOption;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.TimeUnit;

import com.google.common.eventbus.Subscribe;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that JNRPE correctly passes and parse parameters.
 * 
 * @author Massimiliano Ziccardi
 */
public class JnrpeParamTest {

    private String m_sEventType;
    private String m_sEventMessage;

    /**
     * The JNRPE server engine.
     */
    private static JNRPE jnrpeServer;

    /**
     * Constructor.
     */
    public JnrpeParamTest() {

    }

    private static class TestEventListener {
        @Subscribe
        public void receive(final LogEvent event) {
            System.out.println("SERVER: >> " + event.getMessage());
        }
    }
    
    @BeforeClass
    public static final void setUp() throws Exception {
        PluginRepository pr = new PluginRepository();
        CommandRepository cr = new CommandRepository();

        PluginDefinition pd = new PluginDefinition("TESTPLUGIN", "This is a test plugin", new TestPlugin())
            .addOption(new PluginOption()
            .setRequired(true).setOption("t").setLongOpt("type").setHasArgs(true));
        pr.addPluginDefinition(pd);

        CommandDefinition cd = new CommandDefinition("TESTCOMMAND", "TESTPLUGIN").addArgument(new CommandOption("type", "$ARG1$"));
        cr.addCommandDefinition(cd);

        jnrpeServer = JNRPEBuilder.forRepositories(pr, cr).withListener(new TestEventListener())
                .acceptHost("127.0.0.1").acceptParams(true).build();
        jnrpeServer.listen("127.0.0.1", 5667, false);
    }

    @AfterClass
    public static final void shutDown() {
        try {
            Thread.sleep(TimeUnit.SECOND.convert(5));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (jnrpeServer != null) {
            jnrpeServer.shutdown();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testNullProviders() {
        JNRPEBuilder.forRepositories(null, null);
    }

    @Test
    public final void testBadCommand() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("BADCOMMAND", "-t", "pippo");

        Assert.assertEquals(Status.UNKNOWN, ret.getStatus());
        Assert.assertEquals(true, ret.getMessage().contains("Bad command"));
    }

    @Test
    public final void testCommandNullPointerException() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("TESTCOMMAND", "NullPointerException");

        Assert.assertEquals(Status.UNKNOWN, ret.getStatus());
        Assert.assertEquals("Plugin [TESTPLUGIN] execution error: Thrown NullPointerException as requested", ret.getMessage());
    }

    @Test
    public final void testCommandReturnNull() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("TESTCOMMAND", "ReturnNull");
        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
        Assert.assertEquals("Expected 'Command [XXX] with args [YYY] returned null' but got : '"
            + ret.getMessage() + "'", true, ret.getMessage().contains("returned null"));
    }

    @Test
    public final void testThrowRuntimeException() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("TESTCOMMAND", "ThrowRuntimeException");
        Assert.assertEquals(Status.UNKNOWN, ret.getStatus());
        Assert.assertEquals("Plugin [TESTPLUGIN] execution error: Thrown RuntimeException as requested", ret.getMessage());
    }
}
