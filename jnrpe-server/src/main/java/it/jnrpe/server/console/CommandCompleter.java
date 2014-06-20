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

import static jline.internal.Preconditions.checkNotNull;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginDefinition;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.console.completer.Completer;

/**
 * The command completer. We cannot use a simple StringCompleter since we have
 * to perform case insensitive completion.
 * 
 * @author Massimiliano Ziccardi
 */
class CommandCompleter implements Completer {

    private final SortedSet<String> strings = new TreeSet<String>();

    public CommandCompleter(final IPluginRepository pluginRepository, final CommandRepository commandRepository) {

        for (PluginDefinition pd : pluginRepository.getAllPlugins()) {
            strings.add(PluginCommand.NAME + pd.getName().toLowerCase());
        }

        for (CommandDefinition cd : commandRepository.getAllCommands()) {
            strings.add(CommandConsoleCommand.NAME + cd.getName().toLowerCase());
        }

        strings.add(ExitCommand.NAME.toLowerCase());
        strings.add(HelpCommand.NAME.toLowerCase());
    }

    public Collection<String> getStrings() {
        return strings;
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        if (buffer == null) {
            candidates.addAll(strings);
        } else {
            for (String match : strings.tailSet(buffer.toLowerCase())) {
                if (!match.startsWith(buffer.toLowerCase())) {
                    break;
                }

                candidates.add(match);
            }
        }

        if (candidates.size() == 1) {
            candidates.set(0, candidates.get(0) + " ");
        }

        return candidates.isEmpty() ? -1 : 0;
    }
}
