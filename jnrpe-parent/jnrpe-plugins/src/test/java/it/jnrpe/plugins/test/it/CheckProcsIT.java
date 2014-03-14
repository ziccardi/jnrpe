/**
 * 
 */
package it.jnrpe.plugins.test.it;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugin.CheckProcs;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;
import junit.framework.Assert;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Simple CheckProcs plugin test cases
 * 
 * @author Frederico Campos
 *
 */
@Test
public class CheckProcsIT implements ITConstants{


    @BeforeTest
    public void setup() throws Exception {
        PluginDefinition checkProcs = 
                PluginRepositoryUtil.loadFromPluginAnnotation(CheckProcs.class);
        ITSetup.getPluginRepository().addPluginDefinition(checkProcs);
    }
    
    @Test
    public final void checkProcsBasic() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		CommandDefinition cd = new CommandDefinition("CHECK_PROCS_BASIC", "CHECK_PROCS");
		cd.addArgument(new CommandOption("warning", "$ARG1$"));
		cr.addCommandDefinition(cd);
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_PROCS_BASIC", "1:");
		Assert.assertEquals(ret.getStatus(), Status.WARNING);
	}
    
    @Test
	public final void checkProcsCommand() throws JNRPEClientException {
    	// surely a java process must be running?
    	String command = "java";
    	if (System.getProperty("os.name").toLowerCase().contains("windows")){
    		command = "java.exe";
    	}
		CommandRepository cr = ITSetup.getCommandRepository();
		
		CommandDefinition cd = new CommandDefinition("CHECK_PROCS_COMMAND", "CHECK_PROCS");
		cd.addArgument(new CommandOption("command", "$ARG1$"));
		cd.addArgument(new CommandOption("warning", "$ARG2$"));
		cr.addCommandDefinition(cd);
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_PROCS_COMMAND", command, "1:");
		Assert.assertEquals(ret.getStatus(), Status.WARNING);
	}

    
}
