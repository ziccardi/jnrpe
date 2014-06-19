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

import java.util.Date;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugin.CheckTime;
import it.jnrpe.test.utils.TestCommandLine;
import it.jnrpe.test.utils.TimeServer;
import it.jnrpe.test.utils.TimeServer.TimeServerDelegate;
import it.jnrpe.utils.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for the check_time plugin.
 * 
 * @author Massimiliano Ziccardi
 */
public class CheckTimeTest implements TimeServerDelegate {
    
    /**
     * Port where the timeserver will listen.
     */
    private static final int TIME_SERVER_PORT = 2000;
    
    /**
     * The time server.
     */
    private TimeServer timeServer;
    
    /**
     * Starts the time server.
     * @throws Exception thrown on error starting the time server
     */
    @BeforeClass
    public final void setup() throws Exception {
        timeServer = new TimeServer(TIME_SERVER_PORT, this);
        timeServer.start();
    }
    
    /**
     * Stops the time server.
     * @throws Exception
     */
    @AfterClass
    public final void shutDown() {
        timeServer.shutdown();
    }

    /**
     * Checks that the test exits with success state if no 
     * threshold are specified and the TimeServer is reachable.
     * 
     * @throws Exception on check exception
     */
    @Test
    public final void testNoThresholds() throws Exception {
        TestCommandLine cl = new TestCommandLine()
            .withOption("hostname", 'H', "127.0.0.1")
            .withOption("port", 'p', "2000");
        
        CheckTime ct = new CheckTime();
        ReturnValue rv = ct.execute(cl);
        Assert.assertEquals(rv.getStatus(), Status.OK);
    }
    
    /**
     * Checks that test exits with a critical state if
     * the TimeServer is unreachable.
     * 
     * @throws Exception on check exception
     */
    @Test
    public final void testNoTimeServer() throws Exception {
        TestCommandLine cl = new TestCommandLine()
            .withOption("hostname", 'H', "127.0.0.1")
            .withOption("port", 'p', "2001");
        
        CheckTime ct = new CheckTime();
        ReturnValue rv = ct.execute(cl);
        Assert.assertEquals(rv.getStatus(), Status.CRITICAL);
    }
    
    /**
     * Checks that the test return a critical status if the 
     * difference between machine time and the time returned
     * by the TimeServer is greater than 5 seconds.
     * 
     * @throws Exception on check exception
     */
    @Test
    public final void testCriticalStatus() throws Exception {
        TestCommandLine cl = new TestCommandLine()
            .withOption("hostname", 'H', "127.0.0.1")
            .withOption("port", 'p', "2000")
            .withOption("critical-variance", 'c', "5:");
        
        long now = System.currentTimeMillis();
        long tenSecondsInFuture = TimeUnit.SECOND.convert(10) + now;
        
        timeServer.setNextTime(new Date(tenSecondsInFuture));
        
        CheckTime ct = new CheckTime();
        ReturnValue rv = ct.execute(cl);
        Assert.assertEquals(rv.getStatus(), Status.CRITICAL);
        System.out.print(rv.getMessage());
    }
    
    /**
     * Called when the timeserver starts.
     */
    public void serverDidStart() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Called when the timeserver stops.
     */
    public void serverDidStop() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Called when the timeserver stops with errors.
     * @param thr the error
     */
    public void serverStoppedWithError(final Throwable thr) {
        // TODO Auto-generated method stub
        
    }
}
