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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The command section of a configuration.
 *
 * @author Massimiliano Ziccardi
 */
class CommandsSection {
    /**
     * The list of commands.
     */
    private List<Command> commandsList = new ArrayList<Command>();

    /**
     * A single command definition.
     *
     * @author Massimiliano Ziccardi
     */
    public static class Command {
        /**
         * The command name.
         */
        private final String commandName;

        /**
         * The plugin name.
         */
        private final String pluginName;

        /**
         * The command line.
         */
        private final String commandLine;

        /**
         * Build the object.
         *
         * @param name
         *            The command name
         * @param plugin
         *            The plugin name
         * @param cl
         *            The command line
         */
        public Command(final String name, final String plugin, final String cl) {
            commandName = name;
            pluginName = plugin;
            commandLine = cl;
        }

        /**
         * @return the command name
         */
        public String getName() {
            return commandName;
        }

        /**
         * @return the plugin name
         */
        public String getPlugin() {
            return pluginName;
        }

        /**
         * @return the command line
         */
        public String getCommandLine() {
            return commandLine;
        }
    }

    /**
     * Adds a command to the section.
     *
     * @param commandName
     *            The command name
     * @param pluginName
     *            The plugin name
     * @param commandLine
     *            The command line
     */
    public void addCommand(final String commandName, final String pluginName, final String commandLine) {
        commandsList.add(new Command(commandName, pluginName, commandLine));
    }

    /**
     * @return all commands
     */
    public Collection<Command> getAllCommands() {
        return commandsList;
    }
}
