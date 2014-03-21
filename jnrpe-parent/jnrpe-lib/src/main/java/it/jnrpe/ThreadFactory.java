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
package it.jnrpe;

import it.jnrpe.commands.CommandInvoker;

import java.net.Socket;

/**
 * This class implements a simple thread factory. Each binding has its own
 * thread factory.
 *
 * @author Massimiliano Ziccardi
 *
 */
class ThreadFactory {
    /**
     * How many milliseconds to wait for a thread to stop.
     */
    private static final int DEFAULT_THREAD_STOP_TIMEOUT = 5000;

    /**
     * Timeout handler.
     */
    private ThreadTimeoutWatcher watchDog = null;

    /**
     * The invoker object.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Constructs a new thread factory.
     *
     * @param threadTimeout
     *            The thread timeout
     * @param cmdInvoker
     *            The command invoker
     */
    public ThreadFactory(final int threadTimeout,
            final CommandInvoker cmdInvoker) {
        this.commandInvoker = cmdInvoker;

        watchDog = new ThreadTimeoutWatcher();
        ThreadTimeoutWatcher.setThreadTimeout(threadTimeout);
        watchDog.start();
    }

    /**
     * Asks the system level thread factory for a new thread.
     *
     * @param attachedSocket
     *            The socket to be served by the thread
     * @return The newly created thread
     */
    public JNRPEServerThread createNewThread(final Socket attachedSocket) {
        JNRPEServerThread t =
                JNRPEServerThreadFactory.getInstance(commandInvoker)
                        .createNewThread(attachedSocket);
        watchDog.watch(t);
        return t;
    }

    /**
     * Stops all the created threads and stops the timeout watcher.
     */
    public void shutdown() {
        try {
            ThreadTimeoutWatcher.stopWatching();
            // Waits for the thread to stop.
            watchDog.join(DEFAULT_THREAD_STOP_TIMEOUT);
        } catch (InterruptedException ie) {
            throw new IllegalStateException(ie);
        }
    }
}
