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
import it.jnrpe.events.IJNRPEEvent;
import it.jnrpe.events.LogEvent;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginOption;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.TimeUnit;

import java.net.UnknownHostException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.eventbus.Subscribe;

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
    private JNRPE jnrpeServer;

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
    public final void setUp() throws Exception {
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
    public final void shutDown() {
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public final void testNullProviders() {
        JNRPEBuilder.forRepositories(null, null);
    }

//    @Test
//    public final void testPortInUse() throws UnknownHostException {
//        IPluginRepository pr = new PluginRepository();
//        CommandRepository cr = new CommandRepository();
//
//        // TODO : this test should be rewritten
//
//        JNRPE instance1 = null, instance2 = null;
//        try {
//            instance1 = JNRPEBuilder.forRepositories(pr, cr).build();
//            instance1.listen("127.0.0.1", 5666);
//
//            instance2 = JNRPEBuilder.forRepositories(pr, cr).withListener(new IJNRPEEventListener() {
//                public void receive(Object sender, IJNRPEEvent event) {
//                    if (event.getEventName().equals(LogEvent.ERROR.name())) {
//                        m_sEventType = event.getEventName();
//                        m_sEventMessage = (String) event.getEventParams().get("MESSAGE");
//                    }
//                }
//            }).build();
//            instance2.listen("127.0.0.1", 5666);
//
//            try {
//                Thread.sleep(TimeUnit.SECOND.convert(3));
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            Assert.assertNotNull(m_sEventMessage, "No event message received");
//            Assert.assertNotNull(m_sEventType, "No event type received");
//            Assert.assertEquals(m_sEventMessage.contains("Unable to listen on"), true, "No 'Unable to listen on' error received");
//        } finally {
//            if (instance1 != null) {
//                instance1.shutdown();
//            }
//            if (instance2 != null) {
//                instance2.shutdown();
//            }
//        }
//    }

    @Test
    public final void testBadCommand() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("BADCOMMAND", "-t", "pippo");

        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
        Assert.assertEquals(ret.getMessage().contains("Bad command"), true);
    }

    @Test
    public final void testCommandNullPointerException() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("TESTCOMMAND", "NullPointerException");

        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
        Assert.assertEquals(ret.getMessage(), "Plugin [TESTPLUGIN] execution error: Thrown NullPointerException as requested");
    }

    @Test
    public final void testCommandReturnNull() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("TESTCOMMAND", "ReturnNull");
        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
        Assert.assertEquals(ret.getMessage().contains("returned null"), true, "Expected 'Command [XXX] with args [YYY] returned null' but got : '"
                + ret.getMessage() + "'");
    }

    @Test
    public final void testThrowRuntimeException() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5667, false);
        ReturnValue ret = client.sendCommand("TESTCOMMAND", "ThrowRuntimeException");
        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
        Assert.assertEquals(ret.getMessage(), "Plugin [TESTPLUGIN] execution error: Thrown RuntimeException as requested");
    }
}
