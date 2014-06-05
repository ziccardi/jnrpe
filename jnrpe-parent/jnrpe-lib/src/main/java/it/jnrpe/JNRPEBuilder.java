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
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.plugins.IPluginRepository;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

public class JNRPEBuilder {

	private final IPluginRepository pluginRepository;
	private final CommandRepository commandRepository;

	private final Collection<String> acceptedHosts = new ArrayList<String>();
	private final Collection<IJNRPEEventListener> eventListeners = new ArrayList<IJNRPEEventListener>();

	private boolean acceptParams = false;

	private int maxAcceptedConnections = JNRPE.DEFAULT_MAX_ACCEPTED_CONNECTIONS;

	private Charset charset = Charset.defaultCharset();

	private JNRPEBuilder(final IPluginRepository pluginRepository,
			final CommandRepository commandRepository) {
		this.pluginRepository = pluginRepository;
		this.commandRepository = commandRepository;
	}

	/**
	 * Entry point for the builder.
	 * 
	 * @param pluginRepository
	 *            The plugin repository
	 * @param commandRepository
	 *            The command repository
	 * @return this
	 */
	public static JNRPEBuilder forRepositories(
			final IPluginRepository pluginRepository,
			final CommandRepository commandRepository) {

		return new JNRPEBuilder(pluginRepository, commandRepository);
	}

	/**
	 * Pass <code>true</code> if $ARGxx$ macros should be expanded.
	 * 
	 * @param accept
	 *            <code>true</code> if $ARGxx$ macros should be expanded.
	 * @return this
	 */
	public JNRPEBuilder acceptParams(final boolean accept) {
		this.acceptParams = accept;
		return this;
	}

	/**
	 * Adds a client host to the list of accepted hosts.
	 * 
	 * @param hostName
	 *            the hostname or ip address
	 * @return this
	 */
	public JNRPEBuilder acceptHost(final String hostName) {
		this.acceptedHosts.add(hostName);
		return this;
	}

	/**
	 * Adds a listener to the list of event listener
	 * 
	 * @param listener
	 *            the listener
	 * @return this
	 */
	public JNRPEBuilder withListener(final IJNRPEEventListener listener) {
		this.eventListeners.add(listener);
		return this;
	}

	/**
	 * Sets the charset to be used.
	 * 
	 * @param charset
	 *            teh charset to be used
	 * @return this
	 */
	public JNRPEBuilder withCharset(final Charset charset) {
		this.charset = charset;
		return this;
	}

	/**
	 * Sets the maximum number of accepted connections.
	 * 
	 * @param maxConnections
	 *            the maximum number of accepted connections.
	 * @return
	 */
	public JNRPEBuilder withMaxAcceptedConnections(final int maxConnections) {
		this.maxAcceptedConnections = maxConnections;
		return this;
	}

	/**
	 * Builds the configured JNRPE instance.
	 * 
	 * @return the configured JNRPE instance
	 */
	public JNRPE build() {
		JNRPE jnrpe = new JNRPE(pluginRepository, commandRepository, charset,
				acceptParams, acceptedHosts, maxAcceptedConnections,
				eventListeners);

		return jnrpe;
	}
}
