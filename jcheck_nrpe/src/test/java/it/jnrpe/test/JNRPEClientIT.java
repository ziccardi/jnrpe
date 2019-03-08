package it.jnrpe.test;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import org.junit.Assert;
import org.junit.Test;

public class JNRPEClientIT {

    /**
     * This test connects to a native NRPE server with SSL and tests that the connection succeeds
     */
    @Test
    public void testSSLConnectionIT() throws Exception {
        JNRPEClient client = new JNRPEClient("127.0.0.1", 5666, true);
        client.enableWeakCipherSuites();
        ReturnValue ret = client.sendQuery();

        Assert.assertEquals(ret.getStatus(), Status.UNKNOWN);
    }
}
