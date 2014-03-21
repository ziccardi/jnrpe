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

import it.jnrpe.JNRPELIB;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.events.EventsUtil;
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.events.LogEvent;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginProxy;

import java.util.Set;
import java.util.regex.Matcher;

/**
 * This class is used to invoke a command.
 *
 * @author Massimiliano Ziccardi
 *
 */
public final class CommandInvoker {
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
    private final Set<IJNRPEEventListener> listenersList;

    /**
     * Builds and initializes the {@link CommandInvoker} object.
     *
     * @param pluginRepo
     *            The plugin repository containing all the plugins that must be
     *            used by this invoker.
     * @param commandRepo
     *            The command repository containing all the commands that must
     *            be used by this invoker.
     * @param listeners
     *            All the listeners
     */
    public CommandInvoker(final IPluginRepository pluginRepo,
            final CommandRepository commandRepo,
            final Set<IJNRPEEventListener> listeners) {
        acceptParams = true;
        pluginRepository = pluginRepo;
        commandRepository = commandRepo;
        listenersList = listeners;
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
    public ReturnValue invoke(final String commandName,
            final String[] argsAry) {
        if (commandName.equals("_NRPE_CHECK")) {
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
     * @return The result of the command
     */
    public ReturnValue
    invoke(final CommandDefinition cd, final String[] argsAry) {
        String pluginName = cd.getPluginName();

        String[] commandLine = cd.getCommandLine();

        if (acceptParams) {
            for (int j = 0;
                    commandLine != null && j < commandLine.length; j++) {
                for (int i = 0; i < argsAry.length; i++) {
                    commandLine[j] =
                            commandLine[j].replaceAll("\\$[Aa][Rr][Gg]"
                                    + (i + 1) + "\\$",
                                    Matcher.quoteReplacement(argsAry[i]));
                }
            }
        }

        PluginProxy plugin =
                (PluginProxy) pluginRepository.getPlugin(pluginName);

        if (plugin == null) {
            EventsUtil.sendEvent(listenersList, this, LogEvent.INFO,
                    "Unable to instantiate plugin named " + pluginName);
            return new ReturnValue(Status.UNKNOWN,
                    "Error instantiating plugin '" + pluginName
                    + "' : bad plugin name?");
        }

        plugin.addListeners(listenersList);

        try {
            if (commandLine != null) {
                return plugin.execute(commandLine);
            } else {
                return plugin.execute(new String[0]);
            }
        } catch (RuntimeException re) {
            return new ReturnValue(Status.UNKNOWN, "Plugin [" + pluginName
                    + "] execution error: " + re.getMessage());
        } catch (Throwable thr) {
            return new ReturnValue(Status.UNKNOWN, "Plugin [" + pluginName
                    + "] execution error: " + thr.getMessage());
        }
    }
}
