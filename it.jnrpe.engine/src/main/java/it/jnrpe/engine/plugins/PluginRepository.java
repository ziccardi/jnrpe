/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.engine.plugins;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import java.util.*;

/**
 * This class serves as a storage repository for all the plugins that are currently installed in the
 * JNRPE instance. It keeps track of these plugins in the computer's memory, allowing for efficient
 * access and retrieval. The purpose of this repository is to provide a centralized location where
 * the JNRPE instance can easily manage and interact with the installed plugins.
 *
 * <p>Plugins are loaded from all the available {@link IPluginRepository} providers.
 */
public class PluginRepository implements IPluginRepository {
  private static PluginRepository INSTANCE;

  private final Map<String, IPlugin> plugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  private PluginRepository() {
    IPluginRepository.getProviders()
        .forEach(
            pluginRepository -> {
              pluginRepository
                  .getAllPlugins()
                  .forEach(plugin -> plugins.put(plugin.getName(), plugin));
            });

    EventManager.info("Plugin repository ready. %d plugin(s) loaded", plugins.size());
  }

  /**
   * Returns a collection of all the plugins available.
   *
   * @return A collection of all the plugins.
   */
  @Override
  public Collection<IPlugin> getAllPlugins() {
    return plugins.values();
  }

  /**
   * Retrieves the plugin with the specified name from the collection of plugins.
   *
   * @param pluginName the name of the plugin to retrieve
   * @return an Optional containing the plugin if found, or an empty Optional if not found
   */
  public Optional<IPlugin> getPlugin(final String pluginName) {
    return Optional.ofNullable(plugins.get(pluginName));
  }

  /**
   * Returns the singleton instance of the PluginRepository class. If the instance does not exist,
   * it creates a new instance and returns it. This method is thread-safe as it is marked as
   * synchronized, ensuring only one thread can access it at a time.
   *
   * @return the singleton instance of the PluginRepository class
   */
  public static synchronized IPluginRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PluginRepository();
    }
    return INSTANCE;
  }
}
