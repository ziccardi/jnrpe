package it.jnrpe.plugins.test;


import it.jnrpe.ReturnValue;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.plugin.CheckDisk;
import org.junit.BeforeClass;
import org.junit.Test;

public class CheckDiskIT {

    private static String LOCAL_PATH="";
    private static final String REMOTE_PATH = "/mnt/space";

    @BeforeClass
    public static void init() {
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOs = osName.startsWith("mac os x");
        if (isMacOs)
        {
            LOCAL_PATH = "/Volumes/JNRPETEST_DISK";
        } else {
            LOCAL_PATH = "/mnt/JNRPETEST_DISK";
        }
    }

    /**
     * The NRPE server used to run the Nagios `check_disk` plugin could reside on a different machine sharing the same disk
     * on different paths. This method replaces the remote path with the local path so that the plugin outputs are comparable.
     * (for example, NRPE could run into a docker image with a `bind` on a local path).
     *
     * @param res
     * @return
     */
    private String fixRemoteResult(String res) {
        return res
                .replaceAll(" inode=\\d+%", "") // Java implementation does not have the inode info
                .replace(REMOTE_PATH, LOCAL_PATH); // Local path can be different than path into the docker image
    }

    @Test
    public void checkDiskParams() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5666, true);
        client.enableWeakCipherSuites();
        ReturnValue ret = client.sendCommand("check_disk", REMOTE_PATH);

        CheckDisk checkDisk = new CheckDisk();
        PluginTester.given(checkDisk)
                .withOption("path", 'p', LOCAL_PATH)
                .expect(ret.getStatus())
                .expect(fixRemoteResult(ret.getMessage())); // remove inode: we don't have them from java
    }
}
