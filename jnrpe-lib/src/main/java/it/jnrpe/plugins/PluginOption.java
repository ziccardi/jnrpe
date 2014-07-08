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

import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;

/**
 * This class describes a plugin option.
 *
 * @author Massimiliano Ziccardi
 */
public final class PluginOption {
    /**
     * The option.
     */
    private String option = null;

    /**
     * Indicate if the option ha arguments.
     */
    private boolean hasArgs = false;

    /**
     * The number of arguments.
     */
    private Integer argsCount = null;

    /**
     * If the option is mandatory.
     */
    private boolean required = false;

    /**
     * If the argument is optional.
     */
    private boolean argsAreOptional = true;

    /**
     * The name of the argument.
     */
    private String argName = null;

    /**
     * Long version of the option.
     */
    private String longOptionName = null;

    /**
     * The type.
     */
    private String optionType = null;

    /**
     * The separator of the values.
     */
    private String argsValueSeparator = null;

    /**
     * The description.
     */
    private String description = null;

    /**
     * Default constructor.
     */
    public PluginOption() {

    }

    /**
     * Returns the option string.
     *
     * @return The option as string
     */
    public String getOption() {
        return option;
    }

    /**
     * Sets the option string. For example, if the plugin must receive the.
     * '--file' option, sOption will be 'file'.
     *
     * @param optionName
     *            The option as string
     * @return this
     */
    public PluginOption setOption(final String optionName) {
        this.option = optionName;
        return this;
    }

    /**
     * Returns true if the option has an argument.
     *
     * @return true if the option has an argument.
     */
    public boolean hasArgs() {
        return hasArgs;
    }

    /**
     * Tells the option that it must accept an argument.
     *
     * @param argsPresent
     *            true if the option has an argument.
     * @return this
     */
    public PluginOption setHasArgs(final boolean argsPresent) {
        this.hasArgs = argsPresent;
        return this;
    }

    /**
     * Returns the number of arguments.
     *
     * @return the number of arguments.
     */
    public Integer getArgsCount() {
        return argsCount;
    }

    /**
     * Sets the number of arguments.
     *
     * @param numberOfArgs
     *            the number of arguments.
     * @return this
     */
    public PluginOption setArgsCount(final Integer numberOfArgs) {
        this.argsCount = numberOfArgs;
        return this;
    }

    /**
     * Returns the string 'true' if required.
     *
     * @return the string 'true' if required.
     */
    public String getRequired() {
        return String.valueOf(required);
    }

    /**
     * Set if the option is required.
     *
     * @param optIsRequired
     *            <code>true</code> if the option is required.
     * @return this
     */
    public PluginOption setRequired(final boolean optIsRequired) {
        this.required = optIsRequired;
        return this;
    }

    /**
     * Used to know if the option has optional arguments.
     *
     * @return <code>true</code> if the option has optional arguments.
     */
    public Boolean getArgsOptional() {
        return argsAreOptional;
    }

    /**
     * Sets if the arguments are mandatory.
     *
     * @param argsOptional
     *            <code>true</code> if the option has optional arguments.
     * @return this
     */
    public PluginOption setArgsOptional(final Boolean argsOptional) {
        argsAreOptional = argsOptional;
        return this;
    }

    /**
     * Returns the name of the argument of this option.
     *
     * @return the name of the argument of this option.
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Sets the name of the argument of this option.
     *
     * @param argumentName
     *            The argument name
     * @return this
     */
    public PluginOption setArgName(final String argumentName) {
        this.argName = argumentName;
        return this;
    }

    /**
     * Returns the long name of this option.
     *
     * @return the long name of this option.
     */
    public String getLongOpt() {
        return longOptionName;
    }

    /**
     * Sets the long name of this option.
     *
     * @param longOptName
     *            the long name of this option.
     * @return this
     */
    public PluginOption setLongOpt(final String longOptName) {
        this.longOptionName = longOptName;
        return this;
    }

    /**
     * Returns the type of this option.
     *
     * @return the type of this option.
     */
    public String getType() {
        return optionType;
    }

    /**
     * Sets the type of this option.
     *
     * @param type
     *            the type of this option.
     * @return this
     */
    public PluginOption setType(final String type) {
        optionType = type;
        return this;
    }

    /**
     * Returns the value separator.
     *
     * @return the value separator.
     */
    public String getValueSeparator() {
        return argsValueSeparator;
    }

    /**
     * Sets the value separator.
     *
     * @param argumentsValueSeparator
     *            the value separator.
     * @return this
     */
    public PluginOption setValueSeparator(final String argumentsValueSeparator) {
        this.argsValueSeparator = argumentsValueSeparator;
        return this;
    }

    /**
     * Returns the description of this option.
     *
     * @return the description of this option.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this option.
     *
     * @param optDescription
     *            the description of this option.
     * @return this
     */
    public PluginOption setDescription(final String optDescription) {
        this.description = optDescription;
        return this;
    }

    /**
     * Convert this {@link PluginOption} to the Option required by Apache.
     * Commons Cli.
     *
     * @return The option object required by commons cli
     */
    Option toOption() {
        DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();

        oBuilder.withShortName(option).withDescription(description).withRequired(required);

        if (longOptionName != null) {
            oBuilder.withLongName(longOptionName);
        }

        if (hasArgs) {
            ArgumentBuilder aBuilder = new ArgumentBuilder();

            if (argName != null) {
                aBuilder.withName(argName);
            }

            if (argsAreOptional) {
                aBuilder.withMinimum(0);
            }

            if (argsCount != null) {
                aBuilder.withMaximum(argsCount);
            } else {
                aBuilder.withMaximum(1);
            }

            if (argsValueSeparator != null && argsValueSeparator.length() != 0) {
                aBuilder.withInitialSeparator(argsValueSeparator.charAt(0));
                aBuilder.withSubsequentSeparator(argsValueSeparator.charAt(0));
            }
            oBuilder.withArgument(aBuilder.create());
        }

        return oBuilder.create();
    }
}
