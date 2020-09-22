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
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.IPlugin;
import java.util.List;

@CommandLine.Command(
    name = "execute",
    description = "Executes a given plugin",
    aliases = {"exec"})
public class ExecCommand implements Runnable {
  @CommandLine.Parameters(index = "0")
  private String pluginName;

  @CommandLine.Parameters(index = "1..*")
  private List<String> pluginParameters;

  private void execute(IPlugin plugin) {
    CommandLine cl = new CommandLine(plugin);
    try {
      cl.parseArgs(pluginParameters.toArray(new String[pluginParameters.size()]));

      ExecutionResult res = plugin.execute();
      final String label;
      switch (res.getStatus()) {
        case OK:
          label = String.format("[%s - OK]", pluginName);
          break;
        case WARNING:
          label = String.format("[%s - WARNING]", pluginName);
          break;
        case CRITICAL:
          label = String.format("[%s - CRITICAL]", pluginName);
          break;
        default:
          label = String.format("[%s - UNKNOWN]", pluginName);
      }
      System.out.printf("%s - %s\n", label, res.getMessage());
    } catch (Exception e) {
      System.out.println("Error executing plugin: " + e.getMessage());
      cl.usage(System.out);
    }
  }

  @Override
  public void run() {
    PluginRepository.getInstance()
        .getPlugin(pluginName)
        .ifPresentOrElse(
            this::execute,
            () -> System.out.printf("No plugin named [%s] has been found\n", pluginName));
  }
}
