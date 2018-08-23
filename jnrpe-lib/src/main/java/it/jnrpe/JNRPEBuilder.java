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

import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Builder for the {@link JNRPE} object.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public final class JNRPEBuilder {

    /**
     * Default read timeout is 10 seconds.
     */
    private static final int DEFAULT_READ_TIMEOUT = 10;

    /**
     * Default write timeout is 60 seconds.
     */
    private static final int DEFAULT_WRITE_TIMEOUT = 60;

    /**
     * The plugin repository.
     */
    private final IPluginRepository pluginRepository;

    /**
     * The command repository.
     */
    private final CommandRepository commandRepository;

    /**
     * The list of accepted hosts.
     */
    private final Collection<String> acceptedHosts = new ArrayList<String>();

    /**
     * The collection of listener objects.
     */
    private final Collection<Object> eventListeners = new ArrayList<Object>(); 
    
    /**
     * Sets if macros ($ARGxx$) should be expanded or not.
     */
    private boolean acceptParams = false;

    /**
     * Maximum number of concurrent connections.
     */
    private int maxAcceptedConnections = JNRPE.DEFAULT_MAX_ACCEPTED_CONNECTIONS;

    /**
     * The JNRPE charset.
     */
    private Charset charset = Charset.defaultCharset();

    /**
     * Read timeout in seconds.
     */
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    /**
     * Write timeout in seconds.
     */
    private int writeTimeout = DEFAULT_WRITE_TIMEOUT;

    /**
     * Constructor.
     * 
     * @param jnrpePluginRepository
     *            The plugin repository
     * @param jnrpeCommandRepository
     *            The command repository
     */
    private JNRPEBuilder(final IPluginRepository jnrpePluginRepository, final CommandRepository jnrpeCommandRepository) {

        if (jnrpePluginRepository == null || jnrpeCommandRepository == null) {
            throw new IllegalArgumentException("Both plugin and command repository can't be null");
        }

        pluginRepository = jnrpePluginRepository;
        commandRepository = jnrpeCommandRepository;
    }

    /**
     * Entry point for the builder.
     * 
     * @param pluginRepository
     *            The plugin repository
     * @param commandRepository
     *            The command repository
    
     * @return this */
    public static JNRPEBuilder forRepositories(final IPluginRepository pluginRepository, final CommandRepository commandRepository) {

        return new JNRPEBuilder(pluginRepository, commandRepository);
    }

    /**
     * Pass <code>true</code> if $ARGxx$ macros should be expanded.
     * 
     * @param accept
     *            <code>true</code> if $ARGxx$ macros should be expanded.
    
     * @return this */
    public JNRPEBuilder acceptParams(final boolean accept) {
        acceptParams = accept;
        return this;
    }

    /**
     * Adds a client host to the list of accepted hosts.
     * 
     * @param hostName
     *            the hostname or ip address
    
     * @return this */
    public JNRPEBuilder acceptHost(final String hostName) {
        acceptedHosts.add(hostName);
        return this;
    }

    /**
     * Sets the charset to be used.
     * 
     * @param newCharset
     *            the charset to be used
    
     * @return this */
    public JNRPEBuilder withCharset(final Charset newCharset) {
        charset = newCharset;
        return this;
    }

    /**
     * Sets the maximum number of accepted connections.
     * 
     * @param maxConnections
     *            the maximum number of accepted connections.
    
     * @return this */
    public JNRPEBuilder withMaxAcceptedConnections(final int maxConnections) {
        maxAcceptedConnections = maxConnections;
        return this;
    }

    /**
     * Sets the read timeout in seconds. Default is
     * {@link #DEFAULT_READ_TIMEOUT} seconds.
     * 
     * @param readTimeoutSecs
     *            the new read timeout in seconds
    
     * @return this */
    public JNRPEBuilder withReadTimeout(final int readTimeoutSecs) {
        readTimeout = readTimeoutSecs;
        return this;
    }

    /**
     * Sets the write timeout in seconds. Default is
     * {@link #DEFAULT_WRITE_TIMEOUT} seconds.
     * 
     * @param writeTimeoutSecs
     *            the new write timeout in seconds
    
     * @return this */
    public JNRPEBuilder withWriteTimeout(final int writeTimeoutSecs) {
        writeTimeout = writeTimeoutSecs;
        return this;
    }

    /**
     * Adds a listener to the collection of listeners.
     * 
     * @param listener the listener object
    
     * @return this */
    public JNRPEBuilder withListener(final Object listener) {
        eventListeners.add(listener);
        return this;
    }
    
    /**
     * Builds the configured JNRPE instance.
     * 
    
     * @return the configured JNRPE instance */
    public JNRPE build() {
        JNRPE jnrpe = new JNRPE(pluginRepository, commandRepository, charset, acceptParams, acceptedHosts, maxAcceptedConnections, readTimeout,
                writeTimeout);

        IJNRPEEventBus eventBus = jnrpe.getExecutionContext().getEventBus();
        
        for (Object obj : eventListeners) {
            eventBus.register(obj);
        }
        
        return jnrpe;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "JNRPEBuilder [pluginRepository=" + pluginRepository + ", commandRepository=" + commandRepository + ", acceptedHosts=" + acceptedHosts
                + ", eventListeners=" + eventListeners + ", acceptParams=" + acceptParams + ", maxAcceptedConnections=" + maxAcceptedConnections
                + ", charset=" + charset + ", readTimeout=" + readTimeout + ", writeTimeout=" + writeTimeout + "]";
    }
}
