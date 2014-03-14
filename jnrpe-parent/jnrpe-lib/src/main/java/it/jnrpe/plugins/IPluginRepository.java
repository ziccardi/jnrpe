package it.jnrpe.plugins;

import java.util.Collection;

public interface IPluginRepository {

    /**
     * Returns an instance of the plugin declared inside the plugin definition.
     * identificated by the passed name.
     *
     * @param name
     *            The name of the plugin to be instantiated.
     * @return The plugin instance
     */
    IPluginInterface getPlugin(final String name);

    /**
     * Returns all the plugin definitions managed by this repository.
     *
     * @return The collection of plugin definitions.
     */
    Collection<PluginDefinition> getAllPlugins();

    /**
     * Adds a plugin definition to this repository.
     *
     * @param pluginDef
     *            The plugin definition to be added.
     */
    void addPluginDefinition(final PluginDefinition pluginDef);

    /**
     * Removes a plugin definition from the repository.
     * @param pluginDef
     *            The plugin to be removed
     */
    void removePluginDefinition(final PluginDefinition pluginDef);
}
