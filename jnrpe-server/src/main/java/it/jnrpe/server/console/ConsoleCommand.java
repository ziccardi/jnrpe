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

public abstract class ConsoleCommand implements IConsoleCommand {
    private final JNRPE jnrpeInstance;
    private final ConsoleReader console;

    public ConsoleCommand(ConsoleReader consoleReader, JNRPE jnrpe) {
        jnrpeInstance = jnrpe;
        console = consoleReader;
    }

    protected JNRPE getJNRPE() {
        return jnrpeInstance;
    }

    protected ConsoleReader getConsole() {
        return console;
    }

    protected void println(final String msg) throws IOException {
        getConsole().println(msg);
    }

    protected String highlight(final String msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Message can't be null");
        }

        return new StringBuffer("\u001B[1m").append(msg).append("\u001B[0m").toString();
    }
}
