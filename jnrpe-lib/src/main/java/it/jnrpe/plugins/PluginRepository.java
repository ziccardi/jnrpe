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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represent the repository of all the installed plugins.
 * 
 * @author Massimiliano Ziccardi
 * 
 * @version $Revision: 1.0 $
 */
public class PluginRepository implements IPluginRepository {
    /**
     * Contains all the plugins declared inside this {@link PluginRepository}
     * instance. The key of the map is the plugin name, while the value is the.
     * plugin definition itself.
     */
    private final Map<String, PluginDefinition> pluginsDefinitionsMap = new ConcurrentHashMap<String, PluginDefinition>();

    /**
     * Adds a plugin definition to this repository.
     * 
     * @param pluginDef
     *            The plugin definition to be added.
     * @see it.jnrpe.plugins.IPluginRepository#addPluginDefinition(PluginDefinition)
     */
    public final void addPluginDefinition(final PluginDefinition pluginDef) {
        pluginsDefinitionsMap.put(pluginDef.getName(), pluginDef);
    }

    /**
     * Removes a plugin definition from the repository.
     * 
     * @param pluginDef
     *            The plugin to be removed
     * @see it.jnrpe.plugins.IPluginRepository#removePluginDefinition(PluginDefinition)
     */
    public final void removePluginDefinition(final PluginDefinition pluginDef) {
        pluginsDefinitionsMap.remove(pluginDef.getName());
    }

    /**
     * Returns the implementation of the plugin identified by the given name.
     * 
     * @param name
     *            The plugin name
    
    
     * @return the plugin identified by the given name * @throws UnknownPluginException
     *             if no plugin with the given name exists. * @see it.jnrpe.plugins.IPluginRepository#getPlugin(String)
     */
    public final IPluginInterface getPlugin(final String name) throws UnknownPluginException {
        PluginDefinition pluginDef = pluginsDefinitionsMap.get(name);
        if (pluginDef == null) {
            throw new UnknownPluginException(name);
        }

        try {
            IPluginInterface pluginInterface = pluginDef.getPluginInterface();

            if (pluginInterface == null) {
                pluginInterface = pluginDef.getPluginClass().newInstance();
            }
            return new PluginProxy(pluginInterface, pluginDef);
        } catch (Exception e) {
            // FIXME : handle this exception
            e.printStackTrace();
        }

        return null;
    }

    /**
    
     * @return all the configured plugins * @see it.jnrpe.plugins.IPluginRepository#getAllPlugins()
     */
    public final Collection<PluginDefinition> getAllPlugins() {
        return pluginsDefinitionsMap.values();
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        final int maxLen = 10;
        return "PluginRepository [pluginsDefinitionsMap="
                + (pluginsDefinitionsMap != null ? toString(pluginsDefinitionsMap.entrySet(), maxLen) : null) + "]";
    }

    /**
     * Method toString.
     * @param collection Collection<?>
     * @param maxLen int
     * @return String
     */
    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(iterator.next());
        }
        builder.append(']');
        return builder.toString();
    }
}
