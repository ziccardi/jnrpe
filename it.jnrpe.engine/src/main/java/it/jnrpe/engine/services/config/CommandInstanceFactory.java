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
package it.jnrpe.engine.services.config;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.plugins.PluginRepository;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.commands.ICommandFactory;
import it.jnrpe.engine.services.commands.ICommandInstance;
import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import org.apache.commons.text.StringTokenizer;
import org.apache.commons.text.matcher.StringMatcherFactory;

public class CommandInstanceFactory implements ICommandFactory {
  private final IJNRPEConfig.CommandConfig commandConfig;
  private final IPluginRepository pluginRepository;

  public CommandInstanceFactory(final IJNRPEConfig.CommandConfig cc) {
    this.pluginRepository = PluginRepository.getInstance();
    this.commandConfig = cc;
  }

  CommandInstanceFactory(
      final IPluginRepository pluginRepository, final IJNRPEConfig.CommandConfig cc) {
    this.pluginRepository = pluginRepository;
    this.commandConfig = cc;
  }

  @Override
  public String getName() {
    return commandConfig.name();
  }

  @Override
  public ICommandInstance instantiate(String... params) {
    return this.pluginRepository
        .getPlugin(this.commandConfig.plugin())
        .<ICommandInstance>map(
            p ->
                () -> {
                  CommandLine cl = new CommandLine(p);
                  try {
                    var commandLineArgs =
                        new StringTokenizer(
                                this.commandConfig.args(),
                                StringMatcherFactory.INSTANCE.spaceMatcher(),
                                StringMatcherFactory.INSTANCE.quoteMatcher())
                            .getTokenArray();

                    for (int i = 0; params != null && i < params.length; i++) {
                      for (int k = 0; k < commandLineArgs.length; k++) {
                        if (commandLineArgs[k].equalsIgnoreCase(String.format("$ARG%d$", i + 1))) {
                          commandLineArgs[k] = params[i];
                        }
                      }
                    }

                    cl.parseArgs(commandLineArgs);

                    ExecutionResult res = p.execute();
                    final String label =
                        switch (res.getStatus()) {
                          case OK -> String.format("[%s - OK]", this.getName());
                          case WARNING -> String.format("[%s - WARNING]", this.getName());
                          case CRITICAL -> String.format("[%s - CRITICAL]", this.getName());
                          default -> String.format("[%s - UNKNOWN]", this.getName());
                        };
                    return new ExecutionResult(
                        String.format("%s - %s", label, res.getMessage()), res.getStatus());
                  } catch (Exception e) {
                    EventManager.error(
                        "Error executing command: [%s]: %s", this.getName(), e.getMessage());

                    return new ExecutionResult(
                        String.format("[%s - UNKNOWN] - Error executing command", this.getName()),
                        Status.UNKNOWN);
                  }
                })
        .orElseGet(
            () -> {
              EventManager.error(
                  "Error executing command '%s': requested plugin '%s' has not been found",
                  this.getName(), this.commandConfig.plugin());
              return () ->
                  new ExecutionResult(
                      String.format("[%s - UNKNOWN] - Error executing command", this.getName()),
                      Status.OK);
            });
  }

  @Override
  public String toString() {
    return "CommandInstanceFactory{"
        + "name='"
        + getName()
        + '\''
        + ", plugin='"
        + commandConfig.plugin()
        + '\''
        + ", args='"
        + commandConfig.args()
        + '\''
        + '}';
  }
}
