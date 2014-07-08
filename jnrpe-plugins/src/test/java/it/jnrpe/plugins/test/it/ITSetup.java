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
package it.jnrpe.plugins.test.it;

import it.jnrpe.JNRPE;
import it.jnrpe.JNRPEBuilder;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.PluginRepository;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This test class just starts the JNRPE server before running the tests stops
 * it as soon as the tests have finished.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
public class ITSetup implements ITConstants {

    private static JNRPE m_jnrpeServer;

    private static PluginRepository m_pluginRepository;
    private static CommandRepository m_commandRepository;

    @BeforeSuite
    public static void setUp() throws Exception {
        m_pluginRepository = new PluginRepository();
        m_commandRepository = new CommandRepository();

        m_jnrpeServer = JNRPEBuilder.forRepositories(m_pluginRepository, m_commandRepository).acceptHost(BIND_ADDRESS).acceptParams(true).build();

        m_jnrpeServer.listen(BIND_ADDRESS, JNRPE_PORT, false);
    }

    @AfterSuite
    public static void shutDown() throws Exception {
        Thread.sleep(5000);
        if (m_jnrpeServer != null)
            m_jnrpeServer.shutdown();
    }

    public static JNRPE getServer() {
        return m_jnrpeServer;
    }

    public static PluginRepository getPluginRepository() {
        if (m_pluginRepository == null) {
            try {
                setUp();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return m_pluginRepository;
    }

    public static CommandRepository getCommandRepository() {
        return m_commandRepository;
    }
}
