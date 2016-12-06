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
import it.jnrpe.Status;
import it.jnrpe.plugin.CheckProcs;
import it.jnrpe.plugin.utils.LinuxShell;
import it.jnrpe.plugin.utils.MacShell;
import it.jnrpe.plugin.utils.Shell;
import it.jnrpe.plugin.utils.WindowsShell;
import it.jnrpe.test.utils.TestContext;
import it.jnrpe.utils.internal.InjectionUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Shell.class, CheckProcs.class})
public class CheckProcsTest {

    private final static int SECOND = 1;
    private final static int MINUTE = 60 * SECOND;
    private final static int HOUR = 60 * MINUTE;
    private final static int DAY = 24 * HOUR;

    private static Shell mockedMacShell;
    private static Shell mockedLinuxShell;
    private static Shell mockedWindowsShell;

    public CheckProcsTest() throws Exception {
        mockedMacShell = mockShell(Shell.OSTYPE.MAC);
        mockedWindowsShell = mockShell(Shell.OSTYPE.WINDOWS);
        mockedLinuxShell = mockShell(Shell.OSTYPE.LINUX);
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

    @Test
    public void parseWindowsOutputTest1() throws Exception {

        // Force Shell to return windows os.
        PowerMockito.mockStatic(Shell.class);
        PowerMockito.when(Shell.getInstance()).thenReturn(new WindowsShell());

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
        Assert.assertTrue("No columns has been extracted from windows output", !cols.isEmpty());
        Assert.assertEquals(cols.get("cpu"), "0");
        Assert.assertEquals(cols.get("command"), "System Idle Process");
        Assert.assertEquals(cols.get("pid"), "0");
        Assert.assertEquals(cols.get("user"), "NT AUTHORITY\\SYSTEM");
        Assert.assertEquals(cols.get("memory"), "24");
    }


    private static String getMockedPsOutput(Shell.OSTYPE ostype) {
        StringBuffer sb = new StringBuffer();

        switch (ostype) {
            case LINUX:
                sb.append("less             42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  less\n");
                sb.append("more             42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  less\n");
                sb.append("cat              42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  less\n");
                sb.append("TESTPROC             42783 42365 user           0    744  2444176   0:00.02 less                                                           501 Fri05pm ttys004  TESTPROC\n");
                sb.append("TESTPROC             42783 42365 user           0    744  2444176   0:00.02 less                                                           501 Fri05pm ttys004  TESTPROC\n");
                sb.append("TESTPROC             42783 42365 user           0    744  2444176   0:00.02 less                                                           501 Fri05pm ttys004  TESTPROC\n");
                sb.append("TESTPROC             42783 42365 user           0    744  2444176   0:00.02 less                                                           501 Fri05pm ttys004  TESTPROC\n");
                return sb.toString();
            case MAC:
                sb.append("/Applications/iT  1304     1 user           0 169152  2784020   0:09.03 /Applications/iTerm.app/Contents/MacOS/iTerm2                      501 10:42am ??       /Applications/iTerm.app/Contents/MacOS/iTerm2");
                sb.append("/System/Library/  1519     1 user           0  27060  2517016   0:00.08 /System/Library/Frameworks/CoreServices.framework/Frameworks/Met   501 10:42am ??       /System/Library/Frameworks/CoreServices.framework/Frameworks/Metadata.framework/Versions/A/Support/mdworker -s mdworker -c MDSImporterWorker -m com.apple.mdworker.shared");
                sb.append("/usr/bin/ssh-age  1549     1 user           0   6204  2443776   0:00.02 /usr/bin/ssh-agent -l                                              501 10:42am ??       /usr/bin/ssh-agent -l");
                sb.append("TESTPROC         42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  TESTPROC\n");
                sb.append("TESTPROC         42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  TESTPROC\n");
                sb.append("TESTPROC         42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  TESTPROC\n");
                sb.append("TESTPROC         42783 42365 user           0    744  2444176   0:00.02 less                                                               501 Fri05pm ttys004  TESTPROC\n");
                return sb.toString();
            case WINDOWS:
                sb.append("\"Image Name\",\"PID\",\"Session Name\",\"Session#\",\"Mem Usage\",\"Status\",\"User Name\",\"CPU Time\",\"Window Title\"\n");
                sb.append("\"System Idle Process\",\"0\",\"Services\",\"0\",\"24 K\",\"Unknown\",\"NT AUTHORITY\\SYSTEM\",\"0:03:37\",\"N/A\"\n");
                sb.append("\"System\",\"4\",\"Services\",\"0\",\"628 K\",\"Unknown\",\"N/A\",\"0:00:08\",\"N/A\"\n");
                sb.append("\"smss.exe\",\"304\",\"Services\",\"0\",\"788 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"csrss.exe\",\"408\",\"Services\",\"0\",\"3,004 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"csrss.exe\",\"456\",\"Console\",\"1\",\"3,712 K\",\"Running\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"wininit.exe\",\"464\",\"Services\",\"0\",\"3,248 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"winlogon.exe\",\"492\",\"Console\",\"1\",\"4,532 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"services.exe\",\"552\",\"Services\",\"0\",\"6,972 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"lsass.exe\",\"560\",\"Services\",\"0\",\"8,304 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"lsm.exe\",\"572\",\"Services\",\"0\",\"2,780 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"svchost.exe\",\"664\",\"Services\",\"0\",\"6,244 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"VBoxService.exe\",\"724\",\"Services\",\"0\",\"4,236 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"TESTPROC.exe\",\"724\",\"Services\",\"0\",\"4,236 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"TESTPROC.exe\",\"724\",\"Services\",\"0\",\"4,236 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"TESTPROC.exe\",\"724\",\"Services\",\"0\",\"4,236 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                sb.append("\"TESTPROC.exe\",\"724\",\"Services\",\"0\",\"4,236 K\",\"Unknown\",\"N/A\",\"0:00:00\",\"N/A\"\n");
                return sb.toString();
        }

        return null;
    }

    /**
     * Test scan matches for a command.
     */
    @Test
    public final void checkProcsCommandPosix() throws Exception {
        // Mocked output
        PowerMockito.mockStatic(Shell.class);
        PowerMockito.when(Shell.getInstance()).thenReturn(mockedLinuxShell);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC")
            .withOption("warning", 'w', "1:")
            .expect(Status.WARNING);

        PluginTester.given(new CheckProcs())
          .withOption("command", 'C', "TESTPROC")
          .withOption("warning", 'w', "1:")
          .withOption("critical", 'c', "5:")
          .expect(Status.WARNING);

        PluginTester.given(new CheckProcs())
          .withOption("command", 'C', "TESTPROC")
          .withOption("warning", 'w', "2:")
          .withOption("critical", 'c', "3:")
          .expect(Status.CRITICAL);
    }

    /**
     * Test scan matches for a command.
     */
    @Test
    public final void checkProcsCommandBSD() throws Exception {
        PowerMockito.mockStatic(Shell.class);
        PowerMockito.when(Shell.getInstance()).thenReturn(mockedMacShell);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC")
            .withOption("warning", 'w', "1:")
            .expect(Status.WARNING);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC")
            .withOption("warning", 'w', "1:")
            .withOption("critical", 'c', "5:")
            .expect(Status.WARNING);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC")
            .withOption("warning", 'w', "2:")
            .withOption("critical", 'c', "3:")
            .expect(Status.CRITICAL);

    }

    /**
     * Test scan matches for a command.
     */
    @Test
    public final void checkProcsCommandWindows() throws Exception {
        PowerMockito.mockStatic(Shell.class);
        PowerMockito.when(Shell.getInstance()).thenReturn(mockedWindowsShell);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC.exe")
            .withOption("warning", 'w', "1:")
            .expect(Status.WARNING);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC.exe")
            .withOption("warning", 'w', "1:")
            .withOption("critical", 'c', "5:")
            .expect(Status.WARNING);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC.exe")
            .withOption("warning", 'w', "2:")
            .withOption("critical", 'c', "3:")
            .expect(Status.CRITICAL);

    }

    /**
     * Test windows idle process.
     */
    @Test
    public final void checkIsWindowsIdleProc() {
        WindowsShell shell = new WindowsShell();

        String str = "\"System Idle Process\",\"0\",\"Services\",\"0\",\"24 K\",\"Unknown\",\"NT AUTHORITY\\SYSTEM\",\"2:05:59\",\"N/A\"";
        String proc = str.replaceAll("\"", "").split(",")[0];
        Assert.assertTrue(shell.isIdleProc(proc));
        str = "\"System\",\"4\",\"Services\",\"0\",\"1 224 K\",\"Unknown\",\"N/A\",\"0:00:40\",\"N/A\"";
        proc = str.replaceAll("\"", "").split(",")[0];
        Assert.assertTrue(shell.isIdleProc(proc));
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
    public final void checkProcsTimeElapsedUnix() throws Exception {
        PowerMockito.mockStatic(Shell.class);
        PowerMockito.when(Shell.getInstance()).thenReturn(mockedLinuxShell);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC")
            .withOption("warning", 'w', ":10")
            .withOption("metric", 'm', "ELAPSED")
            .expect(Status.WARNING);

        PowerMockito.when(Shell.getInstance()).thenReturn(mockedMacShell);

        PluginTester.given(new CheckProcs())
            .withOption("command", 'C', "TESTPROC")
            .withOption("warning", 'w', ":10")
            .withOption("metric", 'm', "ELAPSED")
            .expect(Status.WARNING);
    }


    private static Shell mockShell(Shell.OSTYPE os) throws Exception {

        Class<? extends Shell> shell = null;

        switch (os) {
            case WINDOWS:
                shell = WindowsShell.class;
                break;
            case LINUX:
                shell = LinuxShell.class;
                break;
            case MAC:
                shell = MacShell.class;
                break;
        }

        Shell mockedShell = PowerMockito.mock(shell);
        PowerMockito.when(mockedShell.getOsType()).thenReturn(os);
        PowerMockito.when(mockedShell.executeSystemCommandAndGetOutput(any(String[].class), anyString())).thenReturn(getMockedPsOutput(os));
        PowerMockito.when(mockedShell.isWindows()).thenReturn(os == Shell.OSTYPE.WINDOWS);
        PowerMockito.when(mockedShell.isLinux()).thenReturn(os == Shell.OSTYPE.LINUX);
        PowerMockito.when(mockedShell.isMac()).thenReturn(os == Shell.OSTYPE.MAC);

        return mockedShell;
    }
}
