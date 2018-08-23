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
import it.jnrpe.JNRPELogger;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;

import java.io.IOException;

import jline.console.ConsoleReader;
import jline.console.history.MemoryHistory;

public class JNRPEConsole {

    private final JNRPELogger LOG = new JNRPELogger(this);
    private final JNRPE jnrpeInstance;
    private final IPluginRepository pluginRepository;
    private final CommandRepository commandRepository;

    public JNRPEConsole(final JNRPE jnrpe, final IPluginRepository pr, final CommandRepository cr) {
        jnrpeInstance = jnrpe;
        pluginRepository = pr;
        commandRepository = cr;
    }

    protected String highlight(final String msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Message can't be null");
        }

        return new StringBuffer("\u001B[1m").append(msg).append("\u001B[0m").toString();
    }

    public void start() {
        try {
            boolean exit = false;
            ConsoleReader console = new ConsoleReader();
            console.setPrompt("JNRPE> ");
            console.setHistory(new MemoryHistory());

            console.addCompleter(new CommandCompleter(pluginRepository, commandRepository));

            while (!exit) {
                String commandLine = console.readLine();
                if (commandLine == null || commandLine.trim().length() == 0) {
                    continue;
                }
                try {
                    exit = CommandExecutor.getInstance(console, pluginRepository, commandRepository, jnrpeInstance).executeCommand(commandLine);
                } catch (Exception e) {
                    console.println(highlight("ERROR: ") + e.getMessage());
                    LOG.error(jnrpeInstance.getExecutionContext(), e.getMessage(), e);
                    //jnrpeInstance.getExecutionContext().getEventBus().post(new LogEvent(this, LogEventType.ERROR, e.getMessage(), e));
                }
            }

        } catch (IOException e) {
            LOG.error(jnrpeInstance.getExecutionContext(), e.getMessage(), e);
            //jnrpeInstance.getExecutionContext().getEventBus().post(new LogEvent(this, LogEventType.ERROR, e.getMessage(), e));
        }
    }
}
