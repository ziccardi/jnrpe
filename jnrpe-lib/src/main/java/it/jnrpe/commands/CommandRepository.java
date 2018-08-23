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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This object manages all the configured commands.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public final class CommandRepository {
    /**
     * Contains all the commands. The KEY is the command name, while the value.
     * is the {@link CommandDefinition}.
     */
    private final Map<String, CommandDefinition> commandDefinitionsMap = new ConcurrentHashMap<String, CommandDefinition>();

    /**
     * Adds a new command definition to the repository.
     * 
     * @param commandDef
     *            The command definition to be added
     */
    public void addCommandDefinition(final CommandDefinition commandDef) {
        commandDefinitionsMap.put(commandDef.getName(), commandDef);
    }

    /**
     * Remove the given command definition from the command repository object.
     * 
     * @param commandDef
     *            the command definition to be removed
     */
    public void removeCommandDefinition(final CommandDefinition commandDef) {
        commandDefinitionsMap.remove(commandDef.getName());
    }

    /**
     * Returns all the command definition that involves the given plugin.
     * 
     * @param pluginName
     *            the name of the plugin we are interested in
    
     * @return all the command definition that involves the given plugin */
    public Set<CommandDefinition> getAllCommandDefinition(final String pluginName) {

        Set<CommandDefinition> res = new HashSet<CommandDefinition>();

        for (CommandDefinition cd : commandDefinitionsMap.values()) {
            if (cd.getPluginName().equals(pluginName)) {
                res.add(cd);
            }
        }

        return res;
    }

    /**
     * Returns the named command definition.
     * 
     * @param commandName
     *            The command name
    
     * @return The command definition associated with <code>sName</code>.
     *         <code>null</code> if not found. */
    public CommandDefinition getCommand(final String commandName) {
        return commandDefinitionsMap.get(commandName);
    }

    /**
    
     * @return all the installed commands. */
    public Collection<CommandDefinition> getAllCommands() {
        return commandDefinitionsMap.values();
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "CommandRepository [commandDefinitionsMap=" + commandDefinitionsMap + "]";
    }
}
