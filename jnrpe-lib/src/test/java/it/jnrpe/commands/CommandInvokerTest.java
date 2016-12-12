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
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 */
public class CommandInvokerTest {

	/**
	 * Field pluginRepository.
	 */
	private final PluginRepository pluginRepository = new PluginRepository();;
	/**
	 * Field commandRepository.
	 */
	private final CommandRepository commandRepository = new CommandRepository();
	/**
	 * Field context.
	 */
	private IJNRPEExecutionContext context;
	
	/**
	 * Constructor for CommandInvokerTest.
	 */
	public CommandInvokerTest() {
		initRepositories();
	}

	/**
	 */
	private static class TestContext implements IJNRPEExecutionContext {

	    /**
	     * Field eventBus.
	     */
	    private final IJNRPEEventBus eventBus;
	    
	    /**
	     * Configured charset.
	     */
	    private final Charset charset;

	    /**
	     * Field pluginRepository.
	     */
	    private final IPluginRepository pluginRepository;
	    /**
	     * Field commandRepository.
	     */
	    private final CommandRepository commandRepository;
	    
	    /**
	     * Builds and initializes the context.
	     * 
	    
	     * @param currentCharset
	     *            the configured charset
	     * @param bus IJNRPEEventBus
	     * @param pluginRepo IPluginRepository
	     * @param commandRepo CommandRepository
	     */
	    private TestContext(final IJNRPEEventBus bus, 
	                    final Charset currentCharset,
	                    final IPluginRepository pluginRepo,
	                    final CommandRepository commandRepo) {
	        eventBus = bus;
	        charset = currentCharset;
	        pluginRepository = pluginRepo;
	        commandRepository = commandRepo;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getEventBus()
	     */
	    /**
	     * Method getEventBus.
	     * @return IJNRPEEventBus
	     * @see it.jnrpe.IJNRPEExecutionContext#getEventBus()
	     */
	    public final IJNRPEEventBus getEventBus() {
	        return eventBus;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getCharset()
	     */
	    /**
	     * Method getCharset.
	     * @return Charset
	     * @see it.jnrpe.IJNRPEExecutionContext#getCharset()
	     */
	    public final Charset getCharset() {
	        return charset;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getPluginRepository()
	     */
	    /**
	     * Method getPluginRepository.
	     * @return IPluginRepository
	     */
	    public IPluginRepository getPluginRepository() {
	        return pluginRepository;
	    }

	    /* (non-Javadoc)
	     * @see it.jnrpe.IJNRPEExecutionContext#getCommandRepository()
	     */
	    /**
	     * Method getCommandRepository.
	     * @return CommandRepository
	     */
	    public CommandRepository getCommandRepository() {
	        return commandRepository;
	    }
	    
	}
	
	/**
	 */
	private static class EchoPlugin implements IPluginInterface {

		/**
		 * Method execute.
		 * @param cl ICommandLine
		 * @return ReturnValue
		 * @throws BadThresholdException
		 * @see it.jnrpe.plugins.IPluginInterface#execute(ICommandLine)
		 */
		public ReturnValue execute(final ICommandLine cl)
				throws BadThresholdException {
			return new ReturnValue(Status.OK, cl.getOptionValue("text"));
		}

		/**
		 * Method getPluginDefinition.
		 * @return PluginDefinition
		 */
		private static PluginDefinition getPluginDefinition() {
			PluginDefinition pd = new PluginDefinition("EchoPlugin",
					"Test echo plugin", new EchoPlugin());
			pd.addOption(new PluginOption().setOption("t").setLongOpt("text")
					.setHasArgs(true).setArgName("txtToBeEchoed")
					.setArgsOptional(Boolean.FALSE).setRequired(true));

			return pd;
		}
	}

	/**
	 * Method initRepositories.
	 */
	private void initRepositories() {
		pluginRepository.addPluginDefinition(EchoPlugin.getPluginDefinition());
		CommandDefinition cd = new CommandDefinition("ECHO", "EchoPlugin");
		cd.addArgument(new CommandOption("text", "$ARG1$"));

		commandRepository.addCommandDefinition(cd);
		context = new TestContext(new JNRPEEventBus(), Charset.defaultCharset(), pluginRepository, commandRepository);
	}

	/**
	 * Method testAcceptParams.
	 */
	@Test
	public void testAcceptParams() {

		CommandInvoker invoker = new CommandInvoker(pluginRepository,
				commandRepository, true, context);

		ReturnValue rv = invoker.invoke("ECHO", new String[] { "ECHO ME" });

		Assert.assertEquals("ECHO ME", rv.getMessage());
	}

	/**
	 * Method testDoNotAcceptParams.
	 */
	@Test
	public void testDoNotAcceptParams() {
		CommandInvoker invoker = new CommandInvoker(pluginRepository,
				commandRepository, false, context);

		ReturnValue rv = invoker.invoke("ECHO", new String[] { "ECHO ME" });

		Assert.assertEquals("$ARG1$", rv.getMessage());
	}

}
