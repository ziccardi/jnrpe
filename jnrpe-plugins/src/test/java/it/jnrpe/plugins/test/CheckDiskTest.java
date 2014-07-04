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

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugin.CheckDisk;
import it.jnrpe.test.utils.TestCommandLine;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the check disk plugin.
 * 
 * @author Massimiliano Ziccardi
 *
 */
public class CheckDiskTest {

    /**
     * Path to be tested.
     */
    private static final File TEST_PATH = new File("/");

    /**
     * Extract the free space percent from the check result message.
     * @param msg the check result message.
     * @return the percent
     */
    private float getPercentFromMessage(final String msg) {
        String stats = msg.substring(msg.indexOf('|') + 1);
        String percent = stats.split(";")[0].split("=")[1].replace('%', ' ').trim();
        return Float.valueOf(percent);
    }

    /**
     * Runs the check using the old threshold format and check that the resulting status is 
     * coherent with the free percent.
     * 
     * @throws Exception on check execution error
     */
    @Test(enabled = true)
    public final void checkDiskOldThresholdsOK() throws Exception {
        TestCommandLine tcl = new TestCommandLine().withOption("path", 'p', TEST_PATH.getAbsolutePath())
                .withOption("warning", 'w', "5:10")
                .withOption("critical", 'c', ":5");

        ReturnValue ret = new CheckDisk().execute(tcl);

        float percent = getPercentFromMessage(ret.getMessage());
        switch (ret.getStatus()) {
        case OK:
            if (percent <= 10.0) {
                Assert.fail("Expected a WARNING or CRITICAL state, but OK has been returned for free space of " + percent + "%");
            }
            break;
        case WARNING:
            if (percent <= 5.0 || percent > 10.0) {
                Assert.fail("Expected a OK or CRITICAL state, but WARNING has been returned for free space of " + percent + "%");
            }
            break;
        case CRITICAL:
            if (percent > 5.0) {
                Assert.fail("Expected a OK or WARNING state, but CRITICAL has been returned for free space of " + percent + "%");
            }
            break;
        case UNKNOWN:
        default:
            Assert.fail(ret.getMessage());
        }

    }

    @Test
    public void checkDiskNewThresholdsPctOK() throws Exception {

        TestCommandLine tcl = new TestCommandLine().withOption("path", 'p', TEST_PATH.getAbsolutePath())
                .withOption("th", 'T', "metric=freepct,ok=15..inf,warn=10..15,crit=inf..10,unit=%")
                .withOption("critical", 'c', ":5");

        ReturnValue ret = new CheckDisk().execute(tcl);

        float percent = getPercentFromMessage(ret.getMessage());
        switch (ret.getStatus()) {
        case OK:
            if (percent <= 15.0) {
                Assert.fail("Expected a WARNING or CRITICAL state, but OK has been returned for free space of " + percent + "%");
            }
            break;
        case WARNING:
            if (percent <= 10.0 || percent > 15.0) {
                Assert.fail("Expected a OK or CRITICAL state, but WARNING has been returned for free space of " + percent + "%");
            }
            break;
        case CRITICAL:
            if (percent > 10.0) {
                Assert.fail("Expected a OK or WARNING state, but CRITICAL has been returned for free space of " + percent + "%");
            }
            break;
        case UNKNOWN:
        default:
            Assert.fail(ret.getMessage());
        }
    }

    @Test
    public void checkDiskOldThresholdsWarning() throws Exception {

        TestCommandLine tcl = new TestCommandLine()
                .withOption("path", 'p', TEST_PATH.getAbsolutePath())
                .withOption("warning", 'w', "0:")
                .withOption("critical", 'c', ":0");

        ReturnValue ret = new CheckDisk().execute(tcl);

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test
    public void checkDiskNewThresholdsPctWarning() throws Exception {
        TestCommandLine tcl = new TestCommandLine()
                .withOption("path", 'p', TEST_PATH.getAbsolutePath())
                .withOption("th", 'T', "metric=freepct,ok=inf..0,warn=0..inf,crit=inf..0,unit=%");

        ReturnValue ret = new CheckDisk().execute(tcl);

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    
    @Test
    public void checkDiskOldThresholdsCritical() throws Exception {

        TestCommandLine tcl = new TestCommandLine()
                .withOption("path", 'p', TEST_PATH.getAbsolutePath())
                .withOption("warning", 'w', ":0")
                .withOption("critical", 'c', "0:");

        ReturnValue ret = new CheckDisk().execute(tcl);

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }
      
    @Test
    public void checkDiskNewThresholdsPctCritical() throws Exception {
        TestCommandLine tcl = new TestCommandLine()
                .withOption("path", 'p', TEST_PATH.getAbsolutePath())
                .withOption("th", 'T', "metric=freepct,ok=inf..0,warn=inf..0,crit=0..inf,unit=%");
        
        ReturnValue ret = new CheckDisk().execute(tcl);

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }
}
