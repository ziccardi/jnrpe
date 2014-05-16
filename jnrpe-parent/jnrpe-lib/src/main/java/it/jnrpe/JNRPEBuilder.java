package it.jnrpe;

import it.jnrpe.commands.CommandRepository;
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.plugins.IPluginRepository;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JNRPEBuilder {

	private final Set<IJNRPEEventListener> eventListenersSet = new HashSet<IJNRPEEventListener>();

	private final IPluginRepository pluginRepository;
	private final CommandRepository commandRepository;

	private final Collection<String> acceptedHosts = new ArrayList<String>();
	private final Collection<IJNRPEEventListener> eventListeners = new ArrayList<IJNRPEEventListener>();

	private boolean acceptParams = false;
	private final boolean useSSL = false;

	private Charset charset = Charset.defaultCharset();

	private JNRPEBuilder(final IPluginRepository pluginRepository,
			final CommandRepository commandRepository) {
		this.pluginRepository = pluginRepository;
		this.commandRepository = commandRepository;
	}

	public static JNRPEBuilder forRepositories(
			final IPluginRepository pluginRepository,
			final CommandRepository commandRepository) {

		return new JNRPEBuilder(pluginRepository, commandRepository);
	}

	public JNRPEBuilder acceptParams(final boolean accept) {
		this.acceptParams = accept;
		return this;
	}

	public JNRPEBuilder acceptHost(final String hostName) {
		this.acceptedHosts.add(hostName);
		return this;
	}

	public JNRPEBuilder withListener(final IJNRPEEventListener listener) {
		this.eventListeners.add(listener);
		return this;
	}

	public JNRPEBuilder withCharset(final Charset charset) {
		this.charset = charset;
		return this;
	}

	public JNRPE build() {
		JNRPE jnrpe = new JNRPE(pluginRepository, commandRepository, charset,
				acceptParams, acceptedHosts, eventListeners);

		return jnrpe;
	}
}
