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

import it.jnrpe.JNRPEEventBus;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugin.CheckProcs;
import it.jnrpe.plugin.utils.ShellUtils;
import it.jnrpe.plugins.test.it.ITSetup;
import it.jnrpe.test.utils.TestContext;
import it.jnrpe.utils.internal.InjectionUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CheckProcsTest {

    private final static int SECOND = 1;
    private final static int MINUTE = 60 * SECOND;
    private final static int HOUR = 60 * MINUTE;
    private final static int DAY = 24 * HOUR;

    public CheckProcsTest() {
        // TODO Auto-generated constructor stub
    }

    @Test
    public void convertToSecondsTest() throws Exception {
        // Convert DD-HH:MM:SS to seconds.
        CheckProcs checkProcs = new CheckProcs();

        Method method = CheckProcs.class.getDeclaredMethod("convertToSeconds", String.class);
        method.setAccessible(true);
        int secs = (Integer) method.invoke(checkProcs, "01-12:05:33");

        Assert.assertEquals(secs, 1 * DAY + 12 * HOUR + 5 * MINUTE + 33 * SECOND);

        // Convert HH:MM:SS to seconds.
        secs = (Integer) method.invoke(checkProcs, "12:05:33");
        Assert.assertEquals(secs, 12 * HOUR + 5 * MINUTE + 33 * SECOND);

        // Convert date in format MM:SS to seconds.
        secs = (Integer) method.invoke(checkProcs, "33");
        Assert.assertEquals(secs, 33 * SECOND);

        // Convert date in format SS to seconds.
        secs = (Integer) method.invoke(checkProcs, "05:33");
        Assert.assertEquals(secs, 5 * MINUTE + 33 * SECOND);
    }

    public void parseWindowsOutputTest1() throws Exception {
        CheckProcs checkProcs = new CheckProcs();

        InjectionUtils.inject(checkProcs, new TestContext(new JNRPEEventBus(), Charset.defaultCharset(), null, null));

        Method method = CheckProcs.class.getDeclaredMethod("parseWindowsOutput", String.class);
        method.setAccessible(true);

        String winOutput = "\"Image Name\",\"PID\",\"Session Name\",\"Session#\",\"Mem Usage\",\"Status\",\"User Name\",\"CPU Time\",\"Window Title\""
                + '\n' + "\"System Idle Process\",\"0\",\"Services\",\"0\",\"24 K\",\"Unknown\",\"NT AUTHORITY\\SYSTEM\",\"0:01:39\",\"N/A\"";
        List<Map<String, String>> res = (List<Map<String, String>>) method.invoke(checkProcs, winOutput);

        Assert.assertEquals(res.size(), 1);
        Map<String, String> cols = res.get(0);
        Assert.assertNotNull(cols);
        Assert.assertTrue(!cols.isEmpty(), "No columns has been extracted from windows output");
        Assert.assertEquals(cols.get("cpu"), "0");
        Assert.assertEquals(cols.get("command"), "System Idle Process");
        Assert.assertEquals(cols.get("pid"), "0");
        Assert.assertEquals(cols.get("user"), "NT AUTHORITY\\SYSTEM");
        Assert.assertEquals(cols.get("memory"), "24");
    }

    // #BUG 17 - https://sourceforge.net/p/jnrpe/bugs/17/
    @Test
    public void parseWindowsOutputTest2() throws Exception {
        CheckProcs checkProcs = new CheckProcs();
        InjectionUtils.inject(checkProcs, new TestContext(new JNRPEEventBus(), Charset.defaultCharset(), null, null));
        // checkProcs.setContext(new TestContext(new JNRPEEventBus(),
        // Charset.defaultCharset(), null, null));
        //
        String winOutput = "\"Image Name\",\"PID\",\"Session Name\",\"Session#\",\"Mem Usage\",\"Status\",\"User Name\",\"CPU Time\",\"Window Title\""
                + '\n' + "\"System Idle Process\",\"0\",\"Services\",\"0\",\"24 KB\",\"Unknown\",\"NT AUTHORITY\\SYSTEM\",\"0:01:39\",\"N/A\"";

        Method method = CheckProcs.class.getDeclaredMethod("parseWindowsOutput", String.class);
        method.setAccessible(true);
        List<Map<String, String>> res = (List<Map<String, String>>) method.invoke(checkProcs, winOutput);

        Assert.assertEquals(res.size(), 1);
        Map<String, String> cols = res.get(0);
        Assert.assertNotNull(cols);
        Assert.assertTrue(!cols.isEmpty(), "No columns has been extracted from windows output");
        Assert.assertEquals(cols.get("cpu"), "0");
        Assert.assertEquals(cols.get("command"), "System Idle Process");
        Assert.assertEquals(cols.get("pid"), "0");
        Assert.assertEquals(cols.get("user"), "NT AUTHORITY\\SYSTEM");
        Assert.assertEquals(cols.get("memory"), "24");
    }

    // #BUG 20 - https://sourceforge.net/p/jnrpe/bugs/20/
    @Test
    public void parseWindowsOutputTest3() throws Exception {
        CheckProcs checkProcs = new CheckProcs();
        InjectionUtils.inject(checkProcs, new TestContext(new JNRPEEventBus(), Charset.defaultCharset(), null, null));
        // checkProcs.setContext(new TestContext(new JNRPEEventBus(),
        // Charset.defaultCharset(), null, null));
        //
        String winOutput = "\"Image Name\",\"PID\",\"Session Name\",\"Session#\",\"Mem Usage\",\"Status\",\"User Name\",\"CPU Time\",\"Window Title\""
                + '\n' + "\"reader_sl.exe\",\"3172\",\"Console\",\"1\",\"3.612 K\",\"Running\",\"DSBUILD-WIN64\\astk\",\"0:00:00\",\"N/A\"";

        Method method = CheckProcs.class.getDeclaredMethod("parseWindowsOutput", String.class);
        method.setAccessible(true);
        List<Map<String, String>> res = (List<Map<String, String>>) method.invoke(checkProcs, winOutput);

        Assert.assertEquals(res.size(), 1);
        Map<String, String> cols = res.get(0);
        Assert.assertNotNull(cols);
        Assert.assertTrue(!cols.isEmpty(), "No columns has been extracted from windows output");

        System.out.println(cols);
        Assert.assertEquals(cols.get("cpu"), "0");
        Assert.assertEquals(cols.get("command"), "reader_sl.exe");
        Assert.assertEquals(cols.get("pid"), "3172");
        Assert.assertEquals(cols.get("user"), "DSBUILD-WIN64\\astk");
        Assert.assertEquals(cols.get("memory"), "3612");
    }

    private String getCommand() {
        String command = "java";
        if (ShellUtils.isWindows()) {
            command = "java.exe";
        }
        return command;
    }

    /**
     * Test scan matches for a command.
     */
    @Test
    public final void checkProcsCommand() {
        // surely a java process must be running?

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', getCommand())
            .withOption("warning", 'w', "1:")
            .expect(Status.WARNING);

    }

    /**
     * Test windows idle process.
     */
    @Test
    public final void checkIsWindowsIdleProc() {
        if (ShellUtils.isWindows()) {
            String str = "\"System Idle Process\",\"0\",\"Services\",\"0\",\"24 K\",\"Unknown\",\"NT AUTHORITY\\SYSTEM\",\"2:05:59\",\"N/A\"";
            String proc = str.replaceAll("\"", "").split(",")[0];
            Assert.assertTrue(ShellUtils.isWindowsIdleProc(proc));
            str = "\"System\",\"4\",\"Services\",\"0\",\"1 224 K\",\"Unknown\",\"N/A\",\"0:00:40\",\"N/A\"";
            proc = str.replaceAll("\"", "").split(",")[0];
            Assert.assertTrue(ShellUtils.isWindowsIdleProc(proc));
        }
    }
    
    /**
     * Test basic usage.
     */
    @Test
    public final void checkProcsBasic() {
        
        PluginTester.given(new CheckProcs())
            .withOption("warning", 'w', "1:")
            .expect(Status.WARNING);
        
    }
    
    /**
     * Test proc elapsed time.
     */
    @Test
    public final void checkProcsTimeElapsed() {

        String command = getCommand();

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', command)
            .withOption("warning", 'w', ":10")
            .withOption("metric", 'm', "ELAPSED")
            .expect(Status.WARNING);

    }
}
