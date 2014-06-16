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
package it.jnrpe.commands;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginOption;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.BadThresholdException;

import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandInvokerTest {

	private final PluginRepository pluginRepository = new PluginRepository();;
	private final CommandRepository commandRepository = new CommandRepository();

	public CommandInvokerTest() {
		initRepositories();
	}

	private static class EchoPlugin implements IPluginInterface {

		public ReturnValue execute(ICommandLine cl)
				throws BadThresholdException {
			return new ReturnValue(Status.OK, cl.getOptionValue("text"));
		}

		private static PluginDefinition getPluginDefinition() {
			PluginDefinition pd = new PluginDefinition("EchoPlugin",
					"Test echo plugin", new EchoPlugin());
			pd.addOption(new PluginOption().setOption("t").setLongOpt("text")
					.setHasArgs(true).setArgName("txtToBeEchoed")
					.setArgsOptional(false).setRequired(true));

			return pd;
		}
	}

	private void initRepositories() {
		pluginRepository.addPluginDefinition(EchoPlugin.getPluginDefinition());
		CommandDefinition cd = new CommandDefinition("ECHO", "EchoPlugin");
		cd.addArgument(new CommandOption("text", "$ARG1$"));

		commandRepository.addCommandDefinition(cd);
	}

	@Test
	public void testAcceptParams() {

		CommandInvoker invoker = new CommandInvoker(pluginRepository,
				commandRepository, true, Collections.EMPTY_LIST);

		ReturnValue rv = invoker.invoke("ECHO", new String[] { "ECHO ME" });

		Assert.assertEquals(rv.getMessage(), "ECHO ME");
	}

	@Test
	public void testDoNotAcceptParams() {
		CommandInvoker invoker = new CommandInvoker(pluginRepository,
				commandRepository, false, Collections.EMPTY_LIST);

		ReturnValue rv = invoker.invoke("ECHO", new String[] { "ECHO ME" });

		Assert.assertEquals(rv.getMessage(), "$ARG1$");
	}

}
