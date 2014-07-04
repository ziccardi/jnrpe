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
import it.jnrpe.IJNRPEEventBus;
import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPEEventBus;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginOption;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.BadThresholdException;

import java.nio.charset.Charset;
import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.eventbus.EventBus;

public class CommandInvokerTest {

	private final PluginRepository pluginRepository = new PluginRepository();;
	private final CommandRepository commandRepository = new CommandRepository();
	private IJNRPEExecutionContext context;
	
	public CommandInvokerTest() {
		initRepositories();
	}

	private static class TestContext implements IJNRPEExecutionContext {

	    private final IJNRPEEventBus eventBus;
	    
	    /**
	     * Configured charset.
	     */
	    private final Charset charset;

	    private final IPluginRepository pluginRepository;
	    private final CommandRepository commandRepository;
	    
	    /**
	     * Builds and initializes the context.
	     * 
	     * @param eventListeners
	     *            the list of listeners
	     * @param currentCharset
	     *            the configured charset
	     */
	    public TestContext(final IJNRPEEventBus bus, 
	                    final Charset currentCharset,
	                    final IPluginRepository pluginRepo,
	                    final CommandRepository commandRepo) {
	        this.eventBus = bus;
	        this.charset = currentCharset;
	        this.pluginRepository = pluginRepo;
	        this.commandRepository = commandRepo;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getEventBus()
	     */
	    public final IJNRPEEventBus getEventBus() {
	        return eventBus;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getCharset()
	     */
	    public final Charset getCharset() {
	        return charset;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getPluginRepository()
	     */
	    public IPluginRepository getPluginRepository() {
	        return pluginRepository;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getCommandRepository()
	     */
	    public CommandRepository getCommandRepository() {
	        return commandRepository;
	    }
	    
	}
	
	private static class EchoPlugin implements IPluginInterface {

		public ReturnValue execute(final ICommandLine cl)
				throws BadThresholdException {
			return new ReturnValue(Status.OK, cl.getOptionValue("text"));
		}

		private static PluginDefinition getPluginDefinition() {
			PluginDefinition pd = new PluginDefinition("EchoPlugin",
					"Test echo plugin", new EchoPlugin());
			pd.addOption(new PluginOption().setOption("t").setLongOpt("text")
					.setHasArgs(true).setArgName("txtToBeEchoed")
					.setArgsOptional(Boolean.FALSE).setRequired(true));

			return pd;
		}
	}

	private void initRepositories() {
		pluginRepository.addPluginDefinition(EchoPlugin.getPluginDefinition());
		CommandDefinition cd = new CommandDefinition("ECHO", "EchoPlugin");
		cd.addArgument(new CommandOption("text", "$ARG1$"));

		commandRepository.addCommandDefinition(cd);
		context = new TestContext(new JNRPEEventBus(), Charset.defaultCharset(), pluginRepository, commandRepository);
	}

	@Test
	public void testAcceptParams() {

		CommandInvoker invoker = new CommandInvoker(pluginRepository,
				commandRepository, true, context);

		ReturnValue rv = invoker.invoke("ECHO", new String[] { "ECHO ME" });

		Assert.assertEquals(rv.getMessage(), "ECHO ME");
	}

	@Test
	public void testDoNotAcceptParams() {
		CommandInvoker invoker = new CommandInvoker(pluginRepository,
				commandRepository, false, context);

		ReturnValue rv = invoker.invoke("ECHO", new String[] { "ECHO ME" });

		Assert.assertEquals(rv.getMessage(), "$ARG1$");
	}

}
