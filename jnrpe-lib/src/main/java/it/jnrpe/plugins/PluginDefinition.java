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
package it.jnrpe.plugins;

import java.util.ArrayList;
import java.util.List;

/**
 * This object represent a plugin definition. It is used to describe to JNRPE
 * which parameters a plugin supports
 *
 * @author Massimiliano Ziccardi
 */
public final class PluginDefinition {
    /**
     * The name of the plugin (as parsed from the XML file).
     */
    private final String name;
    /**
     * The Class of the plugin.
     */
    private final Class<? extends IPluginInterface> pluginClass;

    /**
     * The plugin instance.
     */
    private final IPluginInterface pluginInterface;

    /**
     * The plugin description (as parsed from the XML file).
     */
    private final String description;

    /**
     * All the options this plugin supports (as parsed from the XML file).
     */
    private List<PluginOption> pluginOptionsList = new ArrayList<PluginOption>();

    /**
     * Initializes the plugin definition specifying the Class object that
     * represent the plugin. This constructor is used, for example, by JNRPE
     * server where all the plugins are described in an XML file and are loaded
     * with potentially different class loaders.
     *
     * @param pluginName
     *            The plugin name
     * @param pluginDescription
     *            The plugin description
     * @param pluginClazz
     *            The plugin Class object
     */
    public PluginDefinition(final String pluginName, final String pluginDescription, final Class<? extends IPluginInterface> pluginClazz) {
        this.name = pluginName;
        this.pluginClass = pluginClazz;
        this.description = pluginDescription;
        this.pluginInterface = null;
    }

    /**
     * Initializes the plugin definition specifying a plugin instance. This is.
     * useful when you embed JNRPE: with this constructor you can pass a <i>pre.
     * inizialized/configured</i> instance.
     *
     * @param pluginName
     *            The plugin name
     * @param pluginDescription
     *            The plugin description
     * @param pluginInstance
     *            The plugin instance
     */
    public PluginDefinition(final String pluginName, final String pluginDescription, final IPluginInterface pluginInstance) {
        this.name = pluginName;
        this.pluginClass = null;
        this.description = pluginDescription;
        this.pluginInterface = pluginInstance;
    }

    /**
     * Adds a new option to the plugin.
     *
     * @param option
     *            The option
     * @return this
     */
    public PluginDefinition addOption(final PluginOption option) {
        pluginOptionsList.add(option);
        return this;
    }

    /**
     * Returns the plugin name.
     *
     * @return the plugin name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all the plugin options.
     *
     * @return a List of all the plugin option
     */
    public List<PluginOption> getOptions() {
        return pluginOptionsList;
    }

    /**
     * Returns the plugin description.
     *
     * @return The plugin description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the plugin class, if specified.
     *
     * @return the plugin class. <code>null</code> if not specified.
     */
    Class<? extends IPluginInterface> getPluginClass() {
        return pluginClass;
    }

    /**
     * Returns the plugin instance, if present.
     *
     * @return the plugin interface.<code>null</code> if not specified.
     */
    IPluginInterface getPluginInterface() {
        return pluginInterface;
    }
}
