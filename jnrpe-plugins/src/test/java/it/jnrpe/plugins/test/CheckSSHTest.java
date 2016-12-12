package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.plugin.CheckSsh;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CheckSSHTest {

    private static SshServer sshdServer  = SshServer.setUpDefaultServer();
    private static final String USERNAME="TESTUSER";
    private static final String USERPWD="TESTPWD";
    private static final int PORT = 7022;

    @BeforeClass
    public static void setup() throws Exception {
        sshdServer.setPort(PORT);
        sshdServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

        sshdServer.setPasswordAuthenticator(new PasswordAuthenticator() {
            public boolean authenticate(String user, String password, ServerSession serverSession) throws PasswordChangeRequiredException {
                return user.equals(USERNAME) && password.equals(USERPWD);
            }
        });

        sshdServer.start();
    }

    @AfterClass
    public static void shutdown() throws Exception {
        sshdServer.stop();
    }

    @Test
    @Ignore
    public void checkSSH() {
        PluginTester.given(new CheckSsh())
            .withOption("hostname", 'h', "127.0.0.1")
            .withOption("port", 'p', "" + PORT)
            .withOption("username", 'u', USERNAME)
            .withOption("password", 'P', USERPWD)
            .expect(Status.OK);
    }

}
