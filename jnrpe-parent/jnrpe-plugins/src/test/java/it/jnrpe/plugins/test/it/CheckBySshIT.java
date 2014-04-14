package it.jnrpe.plugins.test.it;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugin.CheckBySsh;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Massimiliano Ziccardi
 */

@Test
public class CheckBySshIT implements ITConstants {

	private String TEST_USERNAME;
	private String TEST_PASSWORD;
	private boolean TEST_SSH;

	@BeforeClass
	public void setup() throws Exception {
		if (ITSetup.getPluginRepository() == null) {
			ITSetup.setUp();
		}

		if (System.getProperty("testSSH") != null) {
			TEST_SSH = Boolean.getBoolean("testSSH");
		} else {
			TEST_SSH = false;
		}

		TEST_USERNAME = System.getProperty("ssh.username");
		TEST_PASSWORD = System.getProperty("ssh.password");

		PluginDefinition checkProcs = PluginRepositoryUtil
				.loadFromPluginAnnotation(CheckBySsh.class);
		ITSetup.getPluginRepository().addPluginDefinition(checkProcs);
	}

	@Test
	public final void checkBySSH() throws JNRPEClientException {

		if (!TEST_SSH || TEST_USERNAME == null || TEST_PASSWORD == null) {
			System.out
					.println("SSH test will be skipped. To test it, install an SSH server and "
							+ "run again the tests passing :\n"
							+ "-Dssh.username={YOUR SSH USERNAME}\n"
							+ "-Dssh.password={YOUR_SSH_PASSWORD}\n"
							+ "-DtestSSH=true");
			return;
		}

		CommandRepository cr = ITSetup.getCommandRepository();

		CommandDefinition cd = new CommandDefinition("CHECK_BY_SSH_TEST",
				"CHECK_BY_SSH");
		cd.addArgument(new CommandOption("hostname", "$ARG1$"));
		cd.addArgument(new CommandOption("port", "$ARG2$"));
		cd.addArgument(new CommandOption("username", "$ARG3$"));
		cd.addArgument(new CommandOption("password", "$ARG4$"));
		cd.addArgument(new CommandOption("command", "$ARG5$"));
		cr.addCommandDefinition(cd);

		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_BY_SSH_TEST", "127.0.0.1",
				"22", TEST_USERNAME, TEST_PASSWORD, "ls /tmp");

		Assert.assertEquals(ret.getStatus(), Status.OK);

		// TODO : should check that the output of the command is ok...
	}

}
