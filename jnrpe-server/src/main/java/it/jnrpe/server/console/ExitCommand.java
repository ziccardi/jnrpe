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

import java.io.IOException;

import jline.console.ConsoleReader;
import it.jnrpe.JNRPE;

public class ExitCommand extends ConsoleCommand {

    public final static String NAME="exit";
    
    public ExitCommand(ConsoleReader consoleReader, JNRPE jnrpe) {
        super(consoleReader, jnrpe);
    }

    public boolean execute(String[] args) throws Exception {
        getJNRPE().shutdown();
        return true;
    }

    public String getName() {
        return NAME;
    }

    public String getCommandLine() {
        return "";
    }

    public void printHelp() throws IOException {
        getConsole().println("Command Line : exit");
        getConsole().println("  Exits from the JNRPE console");
    }

}