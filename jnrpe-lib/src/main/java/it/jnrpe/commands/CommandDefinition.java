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

import it.jnrpe.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Container class for command definition configuration.
 *
 * @author Massimiliano Ziccardi
 */
public final class CommandDefinition {
    /**
     * The command name.
     */
    private final String name;
    /**
     * The plugin name.
     */
    private final String pluginName;

    /**
     * The raw list of arguments.
     */
    private String argsString = null;

    /**
     * The list of options related to this command.
     */
    private List<CommandOption> optionsList = new ArrayList<CommandOption>();

    /**
     * Builds and initializes the command definition.
     *
     * @param commandName
     *            The command name
     * @param cmdPluginName
     *            The plugin associated with this command
     */
    public CommandDefinition(final String commandName, final String cmdPluginName) {
        this.name = commandName;
        this.pluginName = cmdPluginName;
    }

    /**
     * Sets the raw arguments of this command.
     *
     * @param args
     *            The command line
     */
    public void setArgs(final String args) {
        argsString = args;
    }

    /**
     * Returns the command name.
     *
     * @return The command name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the plugin associated with this command.
     *
     * @return The name of the plugin associated with this command
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * The raw command line of this command.
     *
     * @return The raw command line
     */
    public String getArgs() {
        return argsString;
    }

    /**
     * Utility function used to quote the characters.
     *
     * @param s
     *            The string to be elaborated
     * @return The string with the quoted characters
     */
    private static String quote(final String s) {
        if (s.indexOf(' ') != -1) {
            return "\"" + s + "\"";
        }
        return s;
    }

    /**
     * Merges the command line definition read from the server config file with.
     * the values received from check_nrpe and produces a clean command line.
     *
     * @return a parsable command line or an empty array for empty command line.
     */
    public String[] getCommandLine() {
        String[] resAry = null;
        String[] argsAry = argsString != null ? split(argsString) : new String[0];
        List<String> argsList = new ArrayList<String>();

        int startIndex = 0;

        for (CommandOption opt : optionsList) {
            String argName = opt.getName();
            String argValueString = opt.getValue();

            argsList.add((argName.length() == 1 ? "-" : "--") + argName);

            if (argValueString != null) {
                argsList.add(quote(argValueString));
            }
        }

        resAry = new String[argsAry.length + argsList.size()];

        for (String argString : argsList) {
            resAry[startIndex++] = argString;
        }

        // vsRes = new String[args.length + m_vArguments.size()];
        System.arraycopy(argsAry, 0, resAry, startIndex, argsAry.length);

        return resAry;
    }

    /**
     * This method splits the command line separating each command and each
     * argument.
     *
     * @param commandLine
     *            The raw command line
     * @return the splitted command line.
     */
    private static String[] split(final String commandLine) {
        return StringUtils.split(commandLine, false);
    }

    /**
     * Adds an option to the command definition.
     *
     * @param arg
     *            The option to be added
     * @return This object.
     */
    public CommandDefinition addArgument(final CommandOption arg) {
        optionsList.add(arg);
        return this;
    }

    /**
     * @return all the command options.
     */
    public Collection<CommandOption> getOptions() {
        return optionsList;
    }
}
