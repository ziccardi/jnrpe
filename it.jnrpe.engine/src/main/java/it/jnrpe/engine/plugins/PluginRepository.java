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
import it.jnrpe.engine.services.events.LogEvent;
import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import java.util.*;

public class PluginRepository implements IPluginRepository {
  private static PluginRepository INSTANCE;

  private final Map<String, IPlugin> plugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  private PluginRepository() {
    IPluginRepository.getInstances()
        .forEach(
            pluginRepository -> {
              pluginRepository
                  .getAllPlugins()
                  .forEach(plugin -> plugins.put(plugin.getName(), plugin));
            });

    EventManager.emit(
        LogEvent.INFO,
        String.format("Plugin repository ready. %d plugin(s) loaded", plugins.size()));

    //    this.plugins
    //        .values()
    //        .forEach(
    //            plugin -> {
    //              System.out.println(plugin.getName());
    //              new CommandLine(plugin).printVersionHelp(System.out);
    //              new CommandLine(plugin).usage(System.out);
    //              System.out.println("**********************************");
    //            });
  }

  @Override
  public Collection<IPlugin> getAllPlugins() {
    return plugins.values();
  }

  public Optional<IPlugin> getPlugin(final String pluginName) {
    return Optional.ofNullable(plugins.get(pluginName));
  }

  public static synchronized IPluginRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new PluginRepository();
    }
    return INSTANCE;
  }
}
