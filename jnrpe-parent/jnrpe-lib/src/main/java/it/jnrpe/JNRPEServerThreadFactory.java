/*
 * Copyright (c) 2008 Massimiliano Ziccardi Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package it.jnrpe;

import it.jnrpe.commands.CommandInvoker;

import java.net.Socket;

/**
 * This class is a system level thread factory. In later version, it will be
 * used to define the behavior of thread creation at the whole server scope.
 *
 * @author Massimiliano Ziccardi
 *
 */
final class JNRPEServerThreadFactory {
    /**
     * The singleton thread factory instance.
     */
    private static volatile JNRPEServerThreadFactory instance = null;
    /**
     * The command invoker.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Builds the object and initializes it with the given command invoker.
     *
     * @param cmdInvoker
     *            The command invoker
     */
    private JNRPEServerThreadFactory(final CommandInvoker cmdInvoker) {
        this.commandInvoker = cmdInvoker;
    }

    /**
     * In this version thread are always created.
     *
     * @param socket
     *            The socked served by the thread
     * @return The newly created thread
     */
    public JNRPEServerThread createNewThread(final Socket socket) {
        return new JNRPEServerThread(socket, commandInvoker);
    }

    /**
     * Return an instance of the system level thread factory.
     *
     * @param commandInvoker
     *            The command Invoker instance.
     * @return The thread factory
     */
    public static JNRPEServerThreadFactory getInstance(
            final CommandInvoker commandInvoker) {
        if (instance == null) {
            synchronized (JNRPEServerThread.class) {
                if (instance == null) {
                    instance = new JNRPEServerThreadFactory(commandInvoker);
                }
            }

        }

        return instance;
    }
}
