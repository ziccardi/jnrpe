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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represent the repository of all the installed plugins.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class PluginRepository implements IPluginRepository {
    /**
     * Contains all the plugins declared inside this {@link PluginRepository}
     * instance. The key of the map is the plugin name, while the value is the.
     * plugin definition itself.
     */
    private final Map<String, PluginDefinition> pluginsDefinitionsMap =
            new ConcurrentHashMap<String, PluginDefinition>();

    /**
     * Adds a plugin definition to this repository.
     *
     * @param pluginDef
     *            The plugin definition to be added.
     */
    public final void addPluginDefinition(final PluginDefinition pluginDef) {
        pluginsDefinitionsMap.put(pluginDef.getName(), pluginDef);
    }

    /**
     * Removes a plugin definition from the repository.
     * @param pluginDef
     *            The plugin to be removed
     */
    public final void removePluginDefinition(final PluginDefinition pluginDef) {
        pluginsDefinitionsMap.remove(pluginDef.getName());
    }

    /**
     * @param name The plugin name
     * @return the plugin identified by the given name
     */
    public final IPluginInterface getPlugin(final String name) {
        PluginDefinition pluginDef = pluginsDefinitionsMap.get(name);
        if (pluginDef == null) {
            return null;
        }

        try {
            IPluginInterface pluginInterface = pluginDef.getPluginInterface();

            if (pluginInterface == null) {
                pluginInterface =
                        pluginDef.getPluginClass()
                        .newInstance();
            }
            return new PluginProxy(pluginInterface, pluginDef);
        } catch (Exception e) {
            // FIXME : handle this exception
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return all the configured plugins
     */
    public final Collection<PluginDefinition> getAllPlugins() {
        return pluginsDefinitionsMap.values();
    }
}
