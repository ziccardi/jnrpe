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
import it.jnrpe.ReturnValue;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandInvoker;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import jline.console.ConsoleReader;

public class CommandConsoleCommand extends ConsoleCommand {

    public final static String NAME="command:";
    
    private final String commandName;
    private final CommandRepository commandRepository;
    private final IPluginRepository pluginRepository;
    
    public CommandConsoleCommand(ConsoleReader consoleReader, JNRPE jnrpe, String commandName, IPluginRepository pr, CommandRepository cr) {
        super(consoleReader, jnrpe);
        this.commandName = commandName;
        this.pluginRepository = pr;
        this.commandRepository = cr;
    }

    public boolean execute(String[] args) throws Exception {
        ReturnValue retVal = new CommandInvoker(pluginRepository, commandRepository, null).invoke(commandName, args);
        getConsole().println(retVal.getMessage());
        return false;
    }

    public String getCommandLine() {
        CommandDefinition commandDef = commandRepository.getCommand(commandName);
        StringBuffer opts;
        
        if (commandDef.getArgs() != null) {
            opts = new StringBuffer(commandDef.getArgs()).append(" ");
        } else {
            opts = new StringBuffer(" ");
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
