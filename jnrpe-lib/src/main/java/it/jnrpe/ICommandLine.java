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
package it.jnrpe;

import java.util.List;

/**
 * This interface represents the command line received by plugin instances.
 *
 * @author Massimiliano Ziccardi
 */
public interface ICommandLine {

    /**
     * Returns the value of the specified option.
     *
     * @param optionName
     *            The option name
     * @return The value of the option
     */
    String getOptionValue(String optionName);

    /**
     * Returns the values associated with the specified option.
     *
     * @param optionName
     *            The option name
     * @return An ordered list of values
     */
    List<String> getOptionValues(String optionName);

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
    String getOptionValue(String optionName, String defaultValue);

    /**
     * Returns the value of the specified option.
     *
     * @param shortOptionName
     *            The option short name
     * @return The option value
     */
    String getOptionValue(char shortOptionName);

    /**
     * Returns the values associated with the specified option.
     *
     * @param shortOptionName
     *            The option short name
     * @return An ordered list of values
     */
    List<String> getOptionValues(char shortOptionName);

    /**
     * Returns the value of the specified option If the option is not present,
     * returns the default value.
     *
     * @param shortOptionName
     *            The option short name
     * @param defaultValue
     *            The default value
     * @return The option value or, if not specified, the default value
     */
    String getOptionValue(char shortOptionName, String defaultValue);

    /**
     * Returns <code>true</code> if the option is present.
     *
     * @param optionName
     *            The option name
     * @return <code>true</code> if the option is present
     */
    boolean hasOption(String optionName);

    /**
     * Returns <code>true</code> if the option is present.
     *
     * @param shortOptionName
     *            The option short name
     * @return <code>true</code> if the specified option is present
     */
    boolean hasOption(char shortOptionName);
}
