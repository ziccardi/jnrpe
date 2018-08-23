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
package it.jnrpe.server.console;

import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPE;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.UnknownPluginException;

import java.util.Map;
import java.util.TreeMap;

import jline.console.ConsoleReader;

import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

/**
 * This class is used to execute a command from the interactive console.
 * 
 * @author Massimiliano Ziccardi
 */
public class CommandExecutor {

    private final Map<String, IConsoleCommand> commandMap = new TreeMap<String, IConsoleCommand>();

    private static CommandExecutor instance = null;

    public static synchronized CommandExecutor getInstance(final ConsoleReader consoleReader, final IPluginRepository pluginRepo, 
                                                           final CommandRepository commandRepo, final JNRPE jnrpe) {
        if (instance == null) {
            instance = new CommandExecutor();
            instance.commandMap.put(ExitCommand.NAME, new ExitCommand(consoleReader, jnrpe));
            instance.commandMap.put(HelpCommand.NAME, new HelpCommand(consoleReader, jnrpe, instance.commandMap));

            for (PluginDefinition pd : pluginRepo.getAllPlugins()) {
                try {
                    instance.commandMap.put(PluginCommand.NAME + pd.getName().toLowerCase(), 
                            new PluginCommand(consoleReader, pluginRepo, jnrpe, pd.getName()));
                } catch (UnknownPluginException e) {
                    // Skip the plugin...
                }
            }

            for (CommandDefinition cd : commandRepo.getAllCommands()) {
                instance.commandMap.put(CommandConsoleCommand.NAME + cd.getName().toLowerCase(),
                        new CommandConsoleCommand(consoleReader, pluginRepo, commandRepo, jnrpe, cd.getName()));
            }
        }

        return instance;
    }

    private IConsoleCommand getCommand(String commandName) {
        return commandMap.get(commandName.toLowerCase());
    }

    public boolean executeCommand(String commandLine) throws Exception {
        StrTokenizer strtok = new StrTokenizer(commandLine, StrMatcher.charMatcher(' '), StrMatcher.quoteMatcher());
        String[] tokensAry = strtok.getTokenArray();
        String commandName = tokensAry[0];
        String[] params;
        if (tokensAry.length == 1) {
            params = new String[0];
        } else {
            params = new String[tokensAry.length - 1];
            System.arraycopy(tokensAry, 1, params, 0, params.length);
        }

        IConsoleCommand command = getCommand(commandName);
        if (command != null) {
            return command.execute(params);
        } else {
            throw new UnknownCommandException("Unknown command :'" + commandName + "'");
        }
    }
}
