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
