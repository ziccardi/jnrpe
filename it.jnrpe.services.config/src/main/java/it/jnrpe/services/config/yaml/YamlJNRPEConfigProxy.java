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
package it.jnrpe.services.config.yaml;

import it.jnrpe.engine.services.config.*;
import it.jnrpe.services.config.yaml.internal.YAMLJNRPEConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class YamlJNRPEConfigProxy implements IJNRPEConfig {

  private static class ServerConfigProxy implements IServerConfig {
    private final List<IBinding> bindings;

    private ServerConfigProxy(YAMLJNRPEConfig.ServerConfig cfg) {
      this.bindings =
          cfg.bindings.stream()
              .map(b -> new BindingConfigProxy(b))
              .collect(Collectors.toCollection(ArrayList<IBinding>::new));
    }

    public List<IBinding> getBindings() {
      return bindings;
    }

    @Override
    public String toString() {
      return "ServerConfig{" + "bindings=" + bindings + '}';
    }
  }

  public static class BindingConfigProxy implements IBinding {
    private final YAMLJNRPEConfig.Binding binding;

    public BindingConfigProxy(final YAMLJNRPEConfig.Binding binding) {
      this.binding = binding;
    }

    public int getPort() {
      return binding.getPort();
    }

    public String getIp() {
      return binding.getIp();
    }

    public boolean isSsl() {
      return binding.isSsl();
    }

    public List<String> getAllow() {
      return binding.getAllow();
    }

    @Override
    public String toString() {
      return "BindingConfigProxy{"
          + "ip='"
          + this.binding.getIp()
          + '\''
          + ", port="
          + this.binding.getPort()
          + ", ssl="
          + this.binding.isSsl()
          + ", allow="
          + this.binding.getAllow()
          + '}';
    }
  }

  private static class CommandsConfigProxy implements ICommandsConfig {
    private final List<ICommandConfig> definitions;

    private CommandsConfigProxy(YAMLJNRPEConfig.CommandsConfig commandsConfig) {
      this.definitions =
          commandsConfig.getDefinitions().stream()
              .map(CommandDefinitionProxy::new)
              .collect(Collectors.toCollection(ArrayList<ICommandConfig>::new));
    }

    public List<ICommandConfig> getDefinitions() {
      return Collections.unmodifiableList(definitions);
    }

    @Override
    public String toString() {
      return "CommandsConfigProxy{" + "definitions=" + definitions + '}';
    }
  }

  public static class CommandDefinitionProxy implements ICommandConfig {
    private final YAMLJNRPEConfig.CommandDefinition commandDefinition;

    public CommandDefinitionProxy(YAMLJNRPEConfig.CommandDefinition commandDefinition) {
      this.commandDefinition = commandDefinition;
    }

    public String getName() {
      return commandDefinition.getName();
    }

    public String getPlugin() {
      return commandDefinition.getPlugin();
    }

    public String getArgs() {
      return commandDefinition.getArgs();
    }

    @Override
    public String toString() {
      return "CommandInitializer{"
          + "name='"
          + commandDefinition.getName()
          + '\''
          + ", plugin='"
          + commandDefinition.getPlugin()
          + '\''
          + ", args='"
          + commandDefinition.getArgs()
          + '\''
          + '}';
    }
  }

  private final IServerConfig serverConfig;
  private final ICommandsConfig commandsConfig;

  public YamlJNRPEConfigProxy(YAMLJNRPEConfig cfg) {
    this.serverConfig = new ServerConfigProxy(cfg.getServer());
    this.commandsConfig = new CommandsConfigProxy(cfg.getCommands());
  }

  @Override
  public IServerConfig getServer() {
    return this.serverConfig;
  }

  @Override
  public ICommandsConfig getCommands() {
    return this.commandsConfig;
  }
}
