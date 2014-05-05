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
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.plugins.IPluginRepository;

import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is the real JNRPE worker. It must be used to start listening for
 * NRPE requests
 * 
 * @author Massimiliano Ziccardi
 */
public final class JNRPE {
	/**
	 * How many ms to wait for joining a thread.
	 */
	private static final int THREAD_JOIN_TIMEOUT = 3000;

	/**
	 * The plugin repository to be used to find the requested plugin.
	 */
	private final IPluginRepository pluginRepository;
	/**
	 * The command repository to be used to find the requested command.
	 */
	private final CommandRepository commandRepository;

	/**
	 * The list of accepted clients.
	 */
	private final List<String> acceptedHostsList = new ArrayList<String>();

	/**
	 * All the listeners.
	 */
	private final Map<String, IJNRPEListener> listenersMap = new HashMap<String, IJNRPEListener>();

	/**
	 * All the listeners.
	 */
	private final Set<IJNRPEEventListener> eventListenersSet = new HashSet<IJNRPEEventListener>();

	private final Charset charset;

	public JNRPE(final IPluginRepository pluginRepo,
			final CommandRepository commandRepo) {
		if (pluginRepo == null) {
			throw new IllegalArgumentException(
					"Plugin repository cannot be null");
		}

		if (commandRepo == null) {
			throw new IllegalArgumentException(
					"Command repository cannot be null");
		}
		pluginRepository = pluginRepo;
		commandRepository = commandRepo;
		charset = Charset.forName("UTF-8");
	}

	/**
	 * Initializes the JNRPE worker.
	 * 
	 * @param pluginRepo
	 *            The repository containing all the installed plugins
	 * @param commandRepo
	 *            The repository containing all the configured commands.
	 */
	public JNRPE(final IPluginRepository pluginRepo,
			final CommandRepository commandRepo, Charset charset) {
		if (pluginRepo == null) {
			throw new IllegalArgumentException(
					"Plugin repository cannot be null");
		}

		if (commandRepo == null) {
			throw new IllegalArgumentException(
					"Command repository cannot be null");
		}
		pluginRepository = pluginRepo;
		commandRepository = commandRepo;
		this.charset = charset;
	}

	/**
	 * Instructs the server to listen to the given IP/port.
	 * 
	 * @param address
	 *            The address to bind to
	 * @param port
	 *            The port to bind to
	 * @throws UnknownHostException
	 *             -
	 */
	public void listen(final String address, final int port)
			throws UnknownHostException {
		listen(address, port, true);
	}

	/**
	 * Adds a new event listener.
	 * 
	 * @param listener
	 *            The event listener to be added
	 */
	public void addEventListener(final IJNRPEEventListener listener) {
		eventListenersSet.add(listener);
	}

	/**
	 * Starts a new thread that listen for requests. The method is <b>not
	 * blocking</b>
	 * 
	 * @param address
	 *            The address to bind to
	 * @param port
	 *            The listening port
	 * @param useSSL
	 *            <code>true</code> if an SSL socket must be created.
	 * @throws UnknownHostException
	 *             -
	 */
	public void listen(final String address, final int port,
			final boolean useSSL) throws UnknownHostException {
		JNRPEExecutionContext ctx = new JNRPEExecutionContext(
				eventListenersSet, charset);

		JNRPEListenerThread bt = new JNRPEListenerThread(ctx, address, port,
				new CommandInvoker(pluginRepository, commandRepository,
						eventListenersSet));

		for (String sAddr : acceptedHostsList) {
			bt.addAcceptedHosts(sAddr);
		}
		if (useSSL) {
			bt.enableSSL();
		}
		bt.start();

		try {
			// Give time to check if the IP/port configuration ar correctly
			// configured
			bt.join(THREAD_JOIN_TIMEOUT);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
		listenersMap.put(address + port, bt);
	}

	/**
	 * Adds an address to the list of accepted hosts.
	 * 
	 * @param address
	 *            The address to accept
	 */
	public void addAcceptedHost(final String address) {
		acceptedHostsList.add(address);
	}

	/**
	 * Shuts down all the listener handled by this instance.
	 */
	public void shutdown() {
		if (listenersMap.isEmpty()) {
			return;
		}

		for (IJNRPEListener listener : listenersMap.values()) {
			listener.shutdown();
		}
	}
}
