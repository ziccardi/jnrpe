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

import it.jnrpe.plugin.service.IPlugin;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class PluginRepository {
  private final File pluginsFolder;
  private final Map<String, IPlugin> pluginsMap = new HashMap<>();

  public PluginRepository() {
    this(null);
  }

  public PluginRepository(final File pluginsFolder) {
    this.pluginsFolder = pluginsFolder;
    this.initialize();
  }

  /** Loads all the plugins into the repo */
  private void initialize() {

    if (pluginsFolder != null) {
      // loop through all the plugins

      return;
    }

    // Load the plugins only from the system classloader
    ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class);
    serviceLoader.forEach(plugin -> pluginsMap.put(plugin.getName(), plugin));
  }

  public Collection<IPlugin> getAllPlugins() {
    return pluginsMap.values();
  }
}
