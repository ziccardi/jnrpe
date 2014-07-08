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
package it.jnrpe.server;

import it.jnrpe.server.CommandsSection.Command;

import java.io.File;
import java.util.Collection;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the configuration objects.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
public class ConfigTest {

	private void testConfig(final JNRPEConfiguration conf) {
		CommandsSection cs = conf.getCommandSection();
		Collection<Command> commands = cs.getAllCommands();

		Assert.assertNotNull(commands, "Error parsing the commands");
		Assert.assertFalse(commands.isEmpty(), "Error parsing the commands");
		Assert.assertEquals(commands.size(), 1,
				"Parsed commands should be exacly 1");

		Command command = commands.iterator().next();
		Assert.assertEquals(command.getName(), "CHECK_AMQ_ENQ",
				"Error parsing command name");
		Assert.assertEquals(command.getPlugin(), "CHECK_JMX",
				"Error parsing plugin name");

		Assert.assertEquals(
				command.getCommandLine().trim(),
				"--url service:jmx:rmi:///jndi/rmi://$ARG1$/karaf-root "
						+ "--username admin --password admin "
						+ "--object org.apache.activemq:type=Broker,brokerName=amq,destinationType=Queue,destinationName=hin_q "
						+ "--attribute EnqueueCount " + "--warning $ARG2$ "
						+ "--critical $ARG3$", "Error parsing command line");
	}

	/**
	 * This test check bug JNRPE-3
	 * (https://jnrpe-tracker.atlassian.net/browse/JNRPE-3?jql=)
	 * 
	 * @throws Exception
	 *             on any error
	 */
	@Test
	public void testIniCommandWithCommaInParams() throws Exception {

		File iniFile = new File(ConfigTest.class.getClassLoader()
				.getResource("JNRPE-3.ini").getFile());

		IniJNRPEConfiguration config = new IniJNRPEConfiguration();
		config.load(iniFile);

		testConfig(config);
	}

	/**
	 * This test check bug JNRPE-3
	 * (https://jnrpe-tracker.atlassian.net/browse/JNRPE-3?jql=)
	 * 
	 * @throws Exception
	 *             on any error
	 */
	@Test
	public void testXmlCommandWithCommaInParams() throws Exception {

		File iniFile = new File(ConfigTest.class.getClassLoader()
				.getResource("JNRPE-3.cfg.xml").getFile());

		XmlJNRPEConfiguration config = new XmlJNRPEConfiguration();
		config.load(iniFile);

		testConfig(config);
	}

}
