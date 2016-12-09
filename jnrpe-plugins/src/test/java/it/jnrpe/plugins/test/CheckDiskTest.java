package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.plugin.CheckDisk;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.Matchers;
/**
 * Created by ziccardi on 07/12/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { CheckDisk.class } )
public class CheckDiskTest {

    @Test
    public final void checkDiskOldThresholdsOK() throws Exception {

        CheckDisk checkDisk = PowerMockito.spy(new CheckDisk());
        PowerMockito.doReturn(90L).when(checkDisk, "getFreeSpace", Matchers.anyString());
        PowerMockito.doReturn(100L).when(checkDisk, "getTotalSpace", Matchers.anyString());

        PluginTester.given(checkDisk)
            .withOption("path", 'p', "/checkDiskTest")
            .withOption("warning", 'w', "5:10")
            .withOption("critical", 'c', ":5")
            .expect(Status.OK);
    }

    @Test
    public final void checkDiskOldThresholdsWarning() throws Exception {

        CheckDisk checkDisk = PowerMockito.spy(new CheckDisk());
        PowerMockito.doReturn(7L).when(checkDisk, "getFreeSpace", Matchers.anyString());
        PowerMockito.doReturn(100L).when(checkDisk, "getTotalSpace", Matchers.anyString());

        PluginTester.given(checkDisk)
            .withOption("path", 'p', "/checkDiskTest")
            .withOption("warning", 'w', "5:10")
            .withOption("critical", 'c', ":5")
            .expect(Status.WARNING);
    }

    @Test
    public final void checkDiskOldThresholdsCritical() throws Exception {

        CheckDisk checkDisk = PowerMockito.spy(new CheckDisk());
        PowerMockito.doReturn(3L).when(checkDisk, "getFreeSpace", Matchers.anyString());
        PowerMockito.doReturn(100L).when(checkDisk, "getTotalSpace", Matchers.anyString());

        PluginTester.given(checkDisk)
            .withOption("path", 'p', "/checkDiskTest")
            .withOption("warning", 'w', "5:10")
            .withOption("critical", 'c', ":5")
            .expect(Status.CRITICAL);
    }

    @Test
    @Ignore("checkDiskNewThresholdsPctOK - Disabled due to a bug in powermock")
    public void checkDiskNewThresholdsPctOK() throws Exception {
        CheckDisk checkDisk = PowerMockito.spy(new CheckDisk());
        PowerMockito.doReturn(90L).when(checkDisk, "getFreeSpace", Matchers.anyString());
        PowerMockito.doReturn(100L).when(checkDisk, "getTotalSpace", Matchers.anyString());

        PluginTester.given(checkDisk)
            .withOption("path", 'p', "/checkDiskTest")
            .withOption("th", 'T', "metric=freepct,ok=15..inf,warn=10..15,crit=inf..10,unit=%")
            .withOption("critical", 'c', ":5")
            .expect(Status.OK);
    }

    @Test
    @Ignore("checkDiskNewThresholdsPctWarning - Disabled due to a bug in powermock")
    public void checkDiskNewThresholdsPctWarning() throws Exception {
        CheckDisk checkDisk = PowerMockito.spy(new CheckDisk());
        PowerMockito.doReturn(13L).when(checkDisk, "getFreeSpace", Matchers.anyString());
        PowerMockito.doReturn(100L).when(checkDisk, "getTotalSpace", Matchers.anyString());

        PluginTester.given(checkDisk)
            .withOption("path", 'p', "/checkDiskTest")
            .withOption("th", 'T', "metric=freepct,ok=15..inf,warn=10..15,crit=inf..10,unit=%")
            .withOption("critical", 'c', ":5")
            .expect(Status.WARNING);
    }
}
