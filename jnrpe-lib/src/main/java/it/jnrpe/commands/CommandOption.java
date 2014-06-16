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

/**
 * This class represent a command Option.
 *
 * @author Massimiliano Ziccardi
 */
public final class CommandOption {
    /**
     * The option name.
     */
    private final String optionName;
    /**
     * The option value.
     */
    private final String optionValue;

    /**
     * Initializes an option that has no value.
     *
     * @param optName
     *            The option name
     */
    public CommandOption(final String optName) {
        this.optionName = optName;
        optionValue = null;
    }

    /**
     * Initializes an option and its value. The value can be an $ARG?$ macro. If
     * that's the case (and if the server is configured to accept macros), it's
     * value is received by check_nrpe.
     *
     * @param optName
     *            The option name
     * @param optValue
     *            The option value
     */
    public CommandOption(final String optName, final String optValue) {
        this.optionName = optName;
        this.optionValue = optValue;
    }

    /**
     * Returns the option name.
     *
     * @return The option name
     */
    public String getName() {
        return optionName;
    }

    /**
     * Returns the option value.
     *
     * @return The argument value
     */
    public String getValue() {
        return optionValue;
    }
}
