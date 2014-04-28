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
    
    /**
     * Test basic usage
     * 
     * @throws JNRPEClientException
     */
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
    
    /**
     * Test scan matches for a command
     *  
     * @throws JNRPEClientException
     */
    @Test
	public final void checkProcsCommand() throws JNRPEClientException {
    	// surely a java process must be running?
    	String command = getCommand();
		CommandRepository cr = ITSetup.getCommandRepository();
		
		CommandDefinition cd = new CommandDefinition("CHECK_PROCS_COMMAND", "CHECK_PROCS");
		cd.addArgument(new CommandOption("command", "$ARG1$"));
		cd.addArgument(new CommandOption("warning", "$ARG2$"));
		cr.addCommandDefinition(cd);
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_PROCS_COMMAND", command, "1:");
		Assert.assertEquals(ret.getStatus(), Status.WARNING);
	}
    
    /**
     * Test proc ellapsed time
     * 
     * @throws JNRPEClientException
     */
    @Test
	public final void checkProcsTimeEllapsed() throws JNRPEClientException {
    	String command = getCommand();    	
		CommandRepository cr = ITSetup.getCommandRepository();		
		CommandDefinition cd = new CommandDefinition("CHECK_PROCS_ELLAPSED", "CHECK_PROCS");
		cd.addArgument(new CommandOption("command", "$ARG1$"));
		cd.addArgument(new CommandOption("warning", "$ARG2$"));
		cd.addArgument(new CommandOption("metric", ""));
		cr.addCommandDefinition(cd);
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_PROCS_COMMAND", command, ":10", "ELLAPSED");
		System.out.println(ret.getMessage());	
		Assert.assertEquals(ret.getStatus(), Status.WARNING);
		
	}
    
    
    private String getCommand(){
    	String command = "java";
    	if (System.getProperty("os.name").toLowerCase().contains("windows")){
    		command = "java.exe";
    	}
    	return command;
    }
}
