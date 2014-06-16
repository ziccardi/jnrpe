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
package it.jnrpe.plugins;

import it.jnrpe.ICommandLine;

import java.util.List;

import org.apache.commons.cli2.CommandLine;

/**
 * Incapsulate the commons cli CommandLine object, so that the plugins have no
 * dependencies against the command line parsing library.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
class PluginCommandLine implements ICommandLine {
	/**
	 * The Apache Commons Cli {@link CommandLine} object.
	 */
	private final CommandLine commandLine;

	/**
	 * Incapsulate the given command line.
	 * 
	 * @param cl
	 *            The command line to be incapsulated
	 */
	public PluginCommandLine(final CommandLine cl) {
		commandLine = cl;
	}

	/**
	 * Returns the value of the specified option.
	 * 
	 * @param optionName
	 *            The option name
	 * @return The value of the option
	 */
	public String getOptionValue(final String optionName) {
		if (optionName.length() == 1) {
			return getOptionValue(optionName.charAt(0));
		}
		return (String) commandLine.getValue("--" + optionName);
	}

	/**
	 * @param optionName
	 *            The name of the option whose values we are searching for.
	 * @return The list of the values.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getOptionValues(final String optionName) {
		if (optionName.length() == 1) {
			return getOptionValues(optionName.charAt(0));
		}

		return commandLine.getValues("--" + optionName);
	}

	/**
	 * Returns the value of the specified option. If the option is not present,
	 * returns the default value.
	 * 
	 * @param optionName
	 *            The option name
	 * @param defaultValue
	 *            The default value
	 * @return The option value or, if not specified, the default value
	 */
	public String getOptionValue(final String optionName,
			final String defaultValue) {
		if (optionName.length() == 1) {
			return getOptionValue(optionName.charAt(0), defaultValue);
		}
		return (String) commandLine.getValue("--" + optionName, defaultValue);
	}

	/**
	 * Returns the value of the specified option.
	 * 
	 * @param shortOption
	 *            The option short name
	 * @return The option value
	 */
	public String getOptionValue(final char shortOption) {
		return (String) commandLine.getValue("-" + shortOption);
	}

	/**
	 * @param shortOption
	 *            The name of the option whose values we are searching for.
	 * @return The list of the values.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getOptionValues(final char shortOption) {
		return commandLine.getValues("-" + shortOption);
	}

	/**
	 * Returns the value of the specified option If the option is not present,
	 * returns the default value.
	 * 
	 * @param shortOption
	 *            The option short name
	 * @param defaultValue
	 *            The default value
	 * @return The option value or, if not specified, the default value
	 */
	public String getOptionValue(final char shortOption,
			final String defaultValue) {
		return (String) commandLine.getValue("-" + shortOption, defaultValue);
	}

	/**
	 * Returns <code>true</code> if the option is present.
	 * 
	 * @param optionName
	 *            The option name
	 * @return <code>true</code> if the option is present
	 */
	public boolean hasOption(final String optionName) {
		if (optionName.length() == 1) {
			return hasOption(optionName.charAt(0));
		}
		return commandLine.hasOption("--" + optionName);
	}

	/**
	 * Returns <code>true</code> if the option is present.
	 * 
	 * @param shortOption
	 *            The option short name
	 * @return <code>true</code> if the specified option is present
	 */
	public boolean hasOption(final char shortOption) {
		return commandLine.hasOption("-" + shortOption);
	}
}
