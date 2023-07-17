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
package it.jnrpe.services.plugin;

import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import java.io.File;
import java.util.*;

/**
 * The class for the Java service plugin repository.
 *
 * <p>This class implements the {@link IPluginRepository} interface and provides a repository for
 * Java service plugins i.e. for plugin implemented as Java9 Services providers..
 */
public class JavaServicePluginRepository implements IPluginRepository {
  private final File pluginsFolder;
  private final Map<String, IPlugin> pluginsMap = new HashMap<>();

  public JavaServicePluginRepository() {
    this(null);
  }

  public JavaServicePluginRepository(final File pluginsFolder) {
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

  @Override
  public Collection<IPlugin> getAllPlugins() {
    return pluginsMap.values();
  }

  @Override
  public Optional<IPlugin> getPlugin(String pluginName) {
    return Optional.ofNullable(pluginsMap.get(pluginName));
  }
}
