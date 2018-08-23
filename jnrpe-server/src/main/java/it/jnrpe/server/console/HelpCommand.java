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

import it.jnrpe.JNRPE;

import java.io.IOException;
import java.util.Map;

import jline.console.ConsoleReader;

public class HelpCommand extends ConsoleCommand {

    public final static String NAME = "help";
    private final Map<String, IConsoleCommand> commandMap;

    public HelpCommand(ConsoleReader consoleReader, JNRPE jnrpe, Map<String, IConsoleCommand> commands) {
        super(consoleReader, jnrpe);
        commandMap = commands;
    }

    public boolean execute(final String[] args) throws Exception {
        if (args == null || args.length == 0) {
            getConsole().println("Available commands are : ");
            for (IConsoleCommand command : commandMap.values()) {
                // getConsole().println("  * \u001B[1m" + command.getName() +
                // "\u001B[0m");
                getConsole().println("  * " + highlight(command.getName()));
            }

            return false;
        }
        if (args.length != 1) {
            getConsole().println("Only one parameter can be specified for the help command");
            return false;
        }

        IConsoleCommand command = commandMap.get(args[0].toLowerCase());
        if (command == null) {
            getConsole().println("Unknown command : '" + args[0] + "'");
            return false;
        }

        command.printHelp();

        return false;
    }

    public String getName() {
        return NAME;
    }

    public String getCommandLine() {
        return "[COMMAND NAME]";
    }

    public void printHelp() throws IOException {
        getConsole().println("Command Line: help " + getCommandLine());
        getConsole().println("   Without parameters shows the list of available commands");
        getConsole().println("   otherwise prints some help about the specified command");
    }

}
