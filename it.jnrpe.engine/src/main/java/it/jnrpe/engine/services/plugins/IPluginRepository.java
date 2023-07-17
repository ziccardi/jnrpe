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
package it.jnrpe.engine.services.plugins;

import java.util.*;

/**
 * The interface for a plugin repository.
 *
 * <p>This interface provides methods for getting plugins by name and getting all plugins.
 */
public interface IPluginRepository {

  /**
   * Gets a list of all plugin repositories.
   *
   * @return A list of all plugin repositories.
   */
  static Collection<IPluginRepository> getProviders() {
    ServiceLoader<IPluginRepository> services = ServiceLoader.load(IPluginRepository.class);
    return services.stream().map(ServiceLoader.Provider::get).toList();
  }

  /**
   * Gets a plugin by name.
   *
   * @param pluginName The name of the plugin.
   * @return The plugin, if it exists.
   */
  Optional<IPlugin> getPlugin(final String pluginName);

  /**
   * Gets all plugins.
   *
   * @return A collection of all plugins.
   */
  Collection<IPlugin> getAllPlugins();
}
