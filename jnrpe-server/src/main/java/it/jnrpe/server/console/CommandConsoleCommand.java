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

import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPE;
import it.jnrpe.ReturnValue;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandInvoker;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;

import java.io.IOException;

import jline.console.ConsoleReader;

import org.apache.commons.lang.StringUtils;

public class CommandConsoleCommand extends ConsoleCommand {

    public final static String NAME = "command:";

    private final String commandName;
    private final CommandRepository commandRepository;
    private final IPluginRepository pluginRepository;
    private final IJNRPEExecutionContext context;
    
    public CommandConsoleCommand(ConsoleReader consoleReader, IPluginRepository pluginRepo, CommandRepository commandRepo, JNRPE jnrpe, String commandName) {
        super(consoleReader, jnrpe);
        this.commandName = commandName;
        this.pluginRepository = pluginRepo;
        this.commandRepository = commandRepo;
        this.context = jnrpe.getExecutionContext();
    }

    public boolean execute(final String[] args) throws Exception {
        ReturnValue retVal = new CommandInvoker(pluginRepository, commandRepository, true, context).invoke(commandName, args);

        if (retVal == null) {
            getConsole().println("An error has occurred executing the plugin. Null result received.");
        } else {
            getConsole().println(retVal.getMessage());
        }

        return false;
    }

    public String getCommandLine() {
        CommandDefinition commandDef = commandRepository.getCommand(commandName);
        StringBuilder opts;

        if (commandDef.getArgs() != null) {
            opts = new StringBuilder(commandDef.getArgs()).append(' ');
        } else {
            opts = new StringBuilder(" ");
        }

        for (CommandOption opt : commandDef.getOptions()) {
            opts.append(opt.getValue());
        }

        String params = opts + StringUtils.join(commandDef.getOptions(), " ");

        return getName() + " " + params;
    }

    public void printHelp() throws IOException {
        getConsole().println("Command Line : " + getCommandLine());
    }

    public String getName() {
        return NAME + commandName;
    }

}
