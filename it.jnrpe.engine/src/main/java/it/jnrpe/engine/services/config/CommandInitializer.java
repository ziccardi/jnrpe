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
import org.apache.commons.text.StringTokenizer;
import org.apache.commons.text.matcher.StringMatcherFactory;

public class CommandInitializer implements ICommandFactory {
  private final IJNRPEConfig.CommandConfig commandConfig;

  public CommandInitializer(final IJNRPEConfig.CommandConfig cc) {
    this.commandConfig = cc;
  }

  @Override
  public String getName() {
    return commandConfig.name();
  }

  @Override
  public ICommandInstance instantiate(String... params) {
    return PluginRepository.getInstance()
        .getPlugin(this.commandConfig.plugin())
        .<ICommandInstance>map(
            p ->
                () -> {
                  CommandLine cl = new CommandLine(p);
                  try {
                    cl.parseArgs(
                        new StringTokenizer(
                                this.commandConfig.args(),
                                StringMatcherFactory.INSTANCE.spaceMatcher(),
                                StringMatcherFactory.INSTANCE.quoteMatcher())
                            .getTokenArray());

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
    return "CommandInitializer{"
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
