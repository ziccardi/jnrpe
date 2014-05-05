package it.jnrpe.plugins;

public class UnknownPluginException extends PluginRepositoryException {

	private static final long serialVersionUID = 4640110400337335265L;

	public UnknownPluginException() {
		super();
	}

	public UnknownPluginException(final String pluginName) {
		super("Unknown plugin '" + pluginName + "'");
	}
}
