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

import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPELIB;
import it.jnrpe.JNRPELogger;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginProxy;
import it.jnrpe.utils.internal.InjectionUtils;

import java.util.regex.Matcher;

/**
 * This class is used to invoke a command.
 * 
 * @author Massimiliano Ziccardi
 * 
 * @version $Revision: 1.0 $
 */
public final class CommandInvoker {
    
    /**
     * Field LOG.
     */
    private final JNRPELogger LOG = new JNRPELogger(this);
    
    /**
     * <code>true</code> if the variable parameters ($ARG?$) must be
     * interpolated.
     */
    private final boolean acceptParams;

    /**
     * The plugin repository to be used to find the plugins.
     */
    private final IPluginRepository pluginRepository;

    /**
     * The command repository to be used to find the commands.
     */
    private final CommandRepository commandRepository;

    /**
     * The listeners.
     */
    private final IJNRPEExecutionContext context;

    /**
     * Builds and initializes the {@link CommandInvoker} object.
     * 
     * @param pluginRepo
     *            The plugin repository containing all the plugins that must be
     *            used by this invoker.
     * @param commandRepo
     *            The command repository containing all the commands that must
     *            be used by this invoker.
     * @param ctx
     *            The execution context
     * @param acceptParams boolean
     */
    public CommandInvoker(final IPluginRepository pluginRepo, final CommandRepository commandRepo, final boolean acceptParams,
            final IJNRPEExecutionContext ctx) {
        this.acceptParams = acceptParams;
        pluginRepository = pluginRepo;
        commandRepository = commandRepo;
        context = ctx;
    }

    /**
     * This method executes built in commands or builds a CommandDefinition to.
     * execute external commands (plugins). The methods also expands the $ARG?$
     * macros.
     * 
     * @param commandName
     *            The name of the command, as configured in the server
     *            configuration XML
     * @param argsAry
     *            The arguments to pass to the command as configured in the
     *            server configuration XML (with the $ARG?$ macros)
     * @return The result of the command 
     */
    public ReturnValue invoke(final String commandName, final String[] argsAry) {
        if ("_NRPE_CHECK".equals(commandName)) {
            return new ReturnValue(Status.OK, JNRPELIB.VERSION);
        }

        CommandDefinition cd = commandRepository.getCommand(commandName);

        if (cd == null) {
            return new ReturnValue(Status.UNKNOWN, "Bad command");
        }

        return invoke(cd, argsAry);
    }

    
    
    /**
     * This method executes external commands (plugins) The methods also expands
     * the $ARG?$ macros.
     * 
     * @param cd
     *            The command definition
     * @param argsAry
     *            The arguments to pass to the command as configured in the
     *            server configuration XML (with the $ARG?$ macros)
    
     * @return The result of the command */
    public ReturnValue invoke(final CommandDefinition cd, final String[] argsAry) {
        String pluginName = cd.getPluginName();
        try {

            String[] commandLine = cd.getCommandLine();

            if (acceptParams) {
                for (int j = 0; j < commandLine.length; j++) {
                    for (int i = 0; i < argsAry.length; i++) {
                        commandLine[j] = commandLine[j].replaceAll("\\$[Aa][Rr][Gg]" + (i + 1) + "\\$", Matcher.quoteReplacement(argsAry[i]));
                    }
                }
            }

            PluginProxy plugin = (PluginProxy) pluginRepository.getPlugin(pluginName);

            if (plugin == null) {
                LOG.info(context, "Unable to instantiate plugin named " + pluginName);
                //context.getEventBus().post(new LogEvent(this, LogEventType.INFO, "Unable to instantiate plugin named " + pluginName));
                //EventsUtil.sendEvent(listenersList, this, LogEvent.INFO, "Unable to instantiate plugin named " + pluginName);
                return new ReturnValue(Status.UNKNOWN, "Error instantiating plugin '" + pluginName + "' : bad plugin name?");
            }

            //plugin.addListeners(listenersList);
            InjectionUtils.inject(plugin, context);
            //plugin.setContext(context);

            return plugin.execute(commandLine);
        } catch (Throwable thr) {
            LOG.error(context, "Plugin [" + pluginName + "] execution error", thr);
            //context.getEventBus().post(new LogEvent(this, LogEventType.ERROR, "Plugin [" + pluginName + "] execution error", thr));
            return new ReturnValue(Status.UNKNOWN, "Plugin [" + pluginName + "] execution error: " + thr.getMessage());
        }
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "CommandInvoker [LOG=" + LOG + ", acceptParams=" + acceptParams + ", pluginRepository=" + pluginRepository + ", commandRepository="
                + commandRepository + ", context=" + context + "]";
    }
}
