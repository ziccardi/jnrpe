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

import it.jnrpe.engine.plugins.PluginRepository;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandInstance;
import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.IPlugin;
import java.util.Optional;

public class CommandDefinition implements ICommandDefinition, Cloneable {
  private String name;
  private String plugin;
  private String args;

  public CommandDefinition() {}

  CommandDefinition(final CommandDefinition cc) {
    this.name = cc.name;
    this.plugin = cc.plugin;
    this.args = cc.args;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPlugin() {
    return plugin;
  }

  public void setPlugin(String plugin) {
    this.plugin = plugin;
  }

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
  }

  @Override
  public CommandDefinition clone() {
    try {
      return (CommandDefinition) super.clone();
    } catch (CloneNotSupportedException cnse) {
      throw new IllegalStateException("Never happens");
    }
  }

  @Override
  public ICommandInstance instantiate(String... params) {
    Optional<IPlugin> pluginInstance = PluginRepository.getInstance().getPlugin(this.plugin);

    //      return () ->
    //          new ExecutionResult(String.format("[%s -> %s](%s)", name, plugin, args),
    // Status.OK);
    return pluginInstance
        .<ICommandInstance>map(iPlugin -> iPlugin::execute)
        .orElseGet(
            () ->
                () ->
                    new ExecutionResult(
                        String.format(
                            "Plugin [%s] required by command [%s] has not been found",
                            name, plugin),
                        Status.OK));
  }

  @Override
  public String toString() {
    return "CommandDefinition{"
        + "name='"
        + name
        + '\''
        + ", plugin='"
        + plugin
        + '\''
        + ", args='"
        + args
        + '\''
        + '}';
  }
}
