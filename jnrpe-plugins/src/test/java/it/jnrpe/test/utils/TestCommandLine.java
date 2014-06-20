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
package it.jnrpe.test.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import it.jnrpe.ICommandLine;

/**
 * A command line implementation to be used to implement unit tests.
 * 
 * @author Massimiliano Ziccardi
 */
public class TestCommandLine implements ICommandLine {

    /**
     * A map containing all the parameters as if they would have been passed 
     * on the command line.
     * Key is the parameter name, while the value is the parameter value.
     */
    private Map<String, String> commandLine = new HashMap<String, String>();
    
    /**
     * A map containing the aliases of a command parameter.
     * An alias is, for example, the long name of a parameter.
     */
    private Map<String, String> aliases = new HashMap<String, String>();
    
    /**
     * Constructor.
     */
    public TestCommandLine() {
        
    }
    
    /**
     * Adds a new option to this command line.
     * 
     * @param longName The long option name
     * @param shortName the short option name
     * @param value The option value. Can be null
     * @return this
     */
    public final TestCommandLine withOption(final String longName, final char shortName, final String value) {
        commandLine.put(longName, value);
        return this;
    }
    
    /**
     * Return the alias used to configure the content of the command line so that you can
     * pass both the option name and the option alias. 
     * @param option the option
     * @return the alias configured inside this command line
     */
    private String toCommandKey(final String option) {
        if (aliases.containsKey(option) && commandLine.containsKey(aliases.get(option))) {
            return aliases.get(option);
        }
        
        return option;
    }
    
    /**
     * Return the value of the option.
     * 
     * @param optionName the option name
     * @return the value
     */
    public final String getOptionValue(final String optionName) {
        return commandLine.get(toCommandKey(optionName));
    }

    /**
     * Return the value of the option.
     * 
     * This method is returns ONLY one value.
     * 
     * @param optionName the option name
     * @return the value
     */
    public final List<String> getOptionValues(final String optionName) {
        return Arrays.asList(new String[]{getOptionValue(optionName)});
    }

    /**
     * Return the value of the option.
     * 
     * @param optionName the option name
     * @param defaultValue value to be returned if the option is not present
     * @return the value
     */
    public final String getOptionValue(final String optionName, final String defaultValue) {
        String ret = getOptionValue(optionName);
        if (ret == null) {
            ret = defaultValue;
        }
        
        return ret;
    }

    /**
     * Return the value of the option.
     * 
     * @param shortOptionName the option name
     * @return the value
     */
    public final String getOptionValue(final char shortOptionName) {
        return getOptionValue(new String(new char[]{shortOptionName}));
    }

    /**
     * Return the value of the option.
     * 
     * This method is returns ONLY one value.
     * 
     * @param shortOptionName the option name
     * @return the value
     */
    public final List<String> getOptionValues(final char shortOptionName) {
        return getOptionValues(new String(new char[]{shortOptionName}));
    }

    /**
     * Return the value of the option.
     * 
     * @param shortOptionName the option name
     * @param defaultValue value to be returned if the option is not present
     * @return the value
     */
    public final String getOptionValue(final char shortOptionName, final String defaultValue) {
        return getOptionValue(new String(new char[]{shortOptionName}), defaultValue);
    }

    /**
     * Checks if an option is present.
     * @param optionName the option name
     * @return <code>true</code> if the option is present
     */
    public final boolean hasOption(final String optionName) {
        return commandLine.containsKey(optionName);
    }

    /**
     * Checks if an option is present.
     * @param shortOptionName the option name
     * @return <code>true</code> if the option is present
     */
    public final boolean hasOption(final char shortOptionName) {
        return commandLine.containsKey(new String(new char[]{shortOptionName}));
    }
}
