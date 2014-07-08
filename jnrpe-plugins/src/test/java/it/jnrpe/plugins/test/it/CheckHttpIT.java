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
package it.jnrpe.plugins.test.it;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.mocks.httpserver.SimpleHttpServer;
import it.jnrpe.utils.PluginRepositoryUtil;
import junit.framework.Assert;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Frederico Campos
 *
 */

@Test
public class CheckHttpIT implements ITConstants {

	private SimpleHttpServer server = null;
	
	private String PORT = SimpleHttpServer.PORT + "";

	/**
	 * running single unit test
	 */
	private boolean single = false;

	@BeforeClass
	public void setup() throws Exception {
		if (ITSetup.getPluginRepository() == null){
			ITSetup.setUp();
			this.single = true;
		}
		
		ClassLoader cl = CheckHttpIT.class.getClassLoader();
		PluginDefinition checkHttp = PluginRepositoryUtil.parseXmlPluginDefinition(cl, 
				cl.getResourceAsStream("check_http_plugin.xml"));
		ITSetup.getPluginRepository().addPluginDefinition(checkHttp);
		server = new SimpleHttpServer();
		server.start();
	}

	@Test
	public final void checkHttpOK() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		cr.addCommandDefinition(new CommandDefinition("CHECK_HTTP_OK", "CHECK_HTTP")
		.addArgument(new CommandOption("hostname", "$ARG1$"))
		.addArgument(new CommandOption("port", "$ARG2$")));

		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_HTTP_OK", "localhost", PORT);
		Assert.assertEquals(ret.getMessage(), Status.OK, ret.getStatus());
	}

	/**
	 * Check expected strings
	 * @throws JNRPEClientException
	 * void
	 */
	@Test
	public final void checkExpected() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		cr.addCommandDefinition(new CommandDefinition("CHECK_EXPECTED", "CHECK_HTTP")
		.addArgument(new CommandOption("hostname", "$ARG1$"))
		.addArgument(new CommandOption("port", "$ARG2$"))
		.addArgument(new CommandOption("expect", "$ARG3$"))
		.addArgument(new CommandOption("useragent", "$ARG4$"))
				);
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);

		ReturnValue ret = client.sendCommand("CHECK_EXPECTED", 
				"localhost",
				PORT,
				"Hello World,This is a paragraph",
				"Java");
		Assert.assertEquals(ret.getMessage(), ret.getStatus(), Status.OK);

		ret = client.sendCommand("CHECK_EXPECTED", 
				"localhost",
				PORT,
				"GET from JNRPE detected",
				"JNRPE");
		Assert.assertEquals(Status.OK, ret.getStatus());

		ret = client.sendCommand("CHECK_EXPECTED", "localhost", PORT, "This will throw a warning!", "Java");
		Assert.assertEquals(ret.getMessage(), Status.WARNING, ret.getStatus());	

	}

	/**
	 * Check regex, tests for case insensitive
	 * @throws JNRPEClientException
	 * void
	 */
	@Test
	public final void checkRegex() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		cr.addCommandDefinition(new CommandDefinition("CHECK_REGEX", "CHECK_HTTP")
		.addArgument(new CommandOption("hostname", "$ARG1$"))
		.addArgument(new CommandOption("port", "$ARG2$"))
		.addArgument(new CommandOption("regex", "$ARG3$"))
		.addArgument(new CommandOption("invert-regex"))
		.addArgument(new CommandOption("eregi"))); // critical when found

		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);

		ReturnValue ret = client.sendCommand("CHECK_REGEX", "localhost", PORT, "(hello world)");
		Assert.assertEquals(ret.getMessage(), Status.CRITICAL, ret.getStatus());
	}

	@Test
	public final void checkPost() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		cr.addCommandDefinition(new CommandDefinition("CHECK_POST", "CHECK_HTTP")
		.addArgument(new CommandOption("hostname", "$ARG1$"))
		.addArgument(new CommandOption("port", "$ARG2$"))
		.addArgument(new CommandOption("post", "$ARG3$"))
		.addArgument(new CommandOption("string", "$ARG4$")));

		String post = "\"param1=val1&param2=val2&param3=val3\"";
		String expectedString = "\"param1:val1,param2:val2,param3:val3\"";

		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);	
		ReturnValue ret = client.sendCommand("CHECK_POST", "localhost", PORT, post, expectedString);		
		Assert.assertEquals(ret.getMessage(), Status.OK, ret.getStatus());

	}    

	/**
	 * Test that a non-localhost connection actually works
	 * 
	 * @throws JNRPEClientException
	 * void
	 */
	@Test
	public final void checkOutsideOK() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		cr.addCommandDefinition(new CommandDefinition("CHECK_OUTSIDE_OK", "CHECK_HTTP")
		.addArgument(new CommandOption("hostname", "$ARG1$")));
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_OUTSIDE_OK", "google.com");
		Assert.assertEquals(ret.getMessage(), Status.OK, ret.getStatus());
	}


	@Test
	public final void checkNobody() throws JNRPEClientException {
		CommandRepository cr = ITSetup.getCommandRepository();
		cr.addCommandDefinition(new CommandDefinition("CHECK_NOBODY", "CHECK_HTTP")
		.addArgument(new CommandOption("hostname", "$ARG1$"))
		.addArgument(new CommandOption("port", "$ARG2$"))
		.addArgument(new CommandOption("string", "$ARG3$"))
		.addArgument(new CommandOption("no-body"))
				);
		JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
		ReturnValue ret = client.sendCommand("CHECK_NOBODY", "localhost", PORT, "200 OK");		
		Assert.assertEquals(ret.getMessage(), Status.OK, ret.getStatus());
	}

	// @todo
	@Test
	public final void checkCertificate() {
		
	}

	// @todo
	@Test
	public final void checkAuth() {

	}

	@AfterClass
	public void tearDown() throws Exception{
		server.stop();
		if (single){
			ITSetup.shutDown();
		}
	}

}
