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
package it.jnrpe.server.commands.plugins;

import it.jnrpe.engine.plugins.PluginRepository;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.IPlugin;
import java.util.Optional;

@CommandLine.Command(name = "help", description = "Get help about the specified plugin")
public class HelpCommand implements Runnable {
  @CommandLine.Parameters(index = "0")
  String pluginName;

  private void printPluginHelp(IPlugin plugin) {
    new CommandLine(plugin.getClass()).usage(System.out);
  }

  @Override
  public void run() {
    final Optional<IPlugin> plugin = PluginRepository.getInstance().getPlugin(pluginName);
    plugin.ifPresentOrElse(
        this::printPluginHelp,
        () -> {
          System.out.printf("No plugins named '%s' have been found%n", pluginName);
        });
  }
}
