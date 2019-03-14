package it.jnrpe.plugins.test;


import it.jnrpe.ReturnValue;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.plugin.CheckDisk;
import org.junit.Test;

public class CheckDiskIT {

    private final static String RAMDISK = "/Volumes/ramdisk";

    @Test
    public void checkDiskParams() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5666, true);
        client.enableWeakCipherSuites();
        ReturnValue ret = client.sendCommand("check_disk_params", RAMDISK);

        CheckDisk checkDisk = new CheckDisk();
        PluginTester.given(checkDisk)
                .withOption("path", 'p', RAMDISK)
                .withOption("warning", 'w', "5:10")
                .withOption("critical", 'c', ":5")
                .expect(ret.getStatus())
                .expect(ret.getMessage().replaceAll(" inode=\\d+%", "")); // remove inode: we don't have them from java
    }
}
