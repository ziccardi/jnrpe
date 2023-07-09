/*******************************************************************************
 * Copyright (C) 2023, Massimiliano Ziccardi
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

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class PluginRepositoryTest {

  private IPlugin genTestPlugin(final String name) {
    return new IPlugin() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public ExecutionResult execute() {
        return null;
      }
    };
  }

  private void addTestPlugin(final IPluginRepository pluginRepository, IPlugin plugin)
      throws Exception {
    // Access the private plugin list field...
    Field pluginsField = PluginRepository.class.getDeclaredField("plugins");
    pluginsField.setAccessible(true);
    var pluginMap = (Map<String, IPlugin>) pluginsField.get(pluginRepository);

    // Add the plugins to the repository
    pluginMap.put(plugin.getName(), plugin);
  }

  @Test
  public void testGetAllPlugins() throws Exception {
    IPluginRepository pluginRepository = PluginRepository.getInstance();
    // Create some sample plugins
    IPlugin plugin1 = genTestPlugin("Plugin1");
    IPlugin plugin2 = genTestPlugin("Plugin2");

    addTestPlugin(pluginRepository, plugin1);
    addTestPlugin(pluginRepository, plugin2);
    var allPlugins = pluginRepository.getAllPlugins();
    // Assert that the collection contains both plugins
    assertTrue(allPlugins.contains(plugin1));
    assertTrue(allPlugins.contains(plugin2));
    // Assert that the size of the collection is correct
    assertEquals(2, allPlugins.size());
  }

  @Test
  public void testGetPlugin() throws Exception {
    IPluginRepository pluginRepository = PluginRepository.getInstance();
    // Create some sample plugins
    IPlugin plugin1 = genTestPlugin("Plugin1");
    IPlugin plugin2 = genTestPlugin("Plugin2");

    // Add the plugins to the repository
    addTestPlugin(pluginRepository, plugin1);
    addTestPlugin(pluginRepository, plugin2);
    // Test positive case - plugin found
    Optional<IPlugin> foundPlugin = pluginRepository.getPlugin("Plugin1");
    assertTrue(foundPlugin.isPresent());
    assertEquals(plugin1, foundPlugin.get());
    // Test negative case - plugin not found
    Optional<IPlugin> notFoundPlugin = pluginRepository.getPlugin("Plugin3");
    assertFalse(notFoundPlugin.isPresent());
  }

  @Test
  public void testGetInstance() {
    IPluginRepository instance1 = PluginRepository.getInstance();
    IPluginRepository instance2 = PluginRepository.getInstance();
    // Assert that both instances are the same
    assertSame(instance1, instance2);
  }
}
