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
package it.jnrpe.plugin.utils;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugins.MetricGatheringException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * SSH utility class
 * 
 * @author Frederico Campos
 * 
 */
public class SshUtils {

    /**
     * Default timeout.
     */
    private static final int DEFAULT_TIMEOUT = 10;

    /**
     * Default HTTP port.
     */
    private static final int DEFAULT_PORT = 22;

    // TODO: this should throw a specific exception
    /**
     * Starts an ssh session
     * 
     * @param cl the command line object
     * @return an ssh session
     * @throws Exception on any error
     */
    public static Session getSession(final ICommandLine cl) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        int timeout = DEFAULT_TIMEOUT;
        int port = cl.hasOption("port") ? Integer.parseInt(cl.getOptionValue("port")) : +DEFAULT_PORT;
        String hostname = cl.getOptionValue("hostname");
        String username = cl.getOptionValue("username");
        String password = cl.getOptionValue("password");
        String key = cl.getOptionValue("key");
        if (cl.hasOption("timeout")) {
            try {
                timeout = Integer.parseInt(cl.getOptionValue("timeout"));
            } catch (NumberFormatException e) {
                throw new MetricGatheringException("Invalid numeric value for timeout.", Status.CRITICAL, e);
            }
        }
        session = jsch.getSession(username, hostname, port);
        if (key == null) {
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
        } else {
            jsch.addIdentity(key);
        }
        session.connect(timeout * 1000);
        return session;
    }

}
