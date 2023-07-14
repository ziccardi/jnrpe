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
package it.jnrpe.services.config.xml;

import it.jnrpe.engine.services.config.*;
import it.jnrpe.services.config.xml.pojo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class XMLJNRPEConfigurationProxy implements IJNRPEConfig {
  private final ServerConfigProxy _serverConfig;
  private final CommandsConfigProxy _commandsConfig;

  private static class BindingConfigProxy implements IBinding {
    private final XMLBind _bind;
    private final String _ip;
    private final int _port;

    private final List<String> _allowList = new ArrayList<>();

    private BindingConfigProxy(XMLBind bind, XMLServerConfiguration serverConf) {
      this._bind = bind;
      var addressComponents = _bind.getAddress().split(":");
      this._ip = addressComponents[0];
      this._port = addressComponents.length > 1 ? Integer.parseInt(addressComponents[1]) : 5666;

      serverConf.getAllowList().forEach(allow -> this._allowList.add(allow.getIp()));
    }

    @Override
    public int getPort() {
      return _port;
    }

    @Override
    public String getIp() {
      return _ip;
    }

    @Override
    public boolean isSsl() {
      return this._bind.isSsl();
    }

    @Override
    public List<String> getAllow() {
      return this._allowList;
    }
  }

  private static class ServerConfigProxy implements IServerConfig {
    private final List<IBinding> _bindings;

    private ServerConfigProxy(XMLServerConfiguration xmlServerConfiguration) {
      this._bindings =
          xmlServerConfiguration.getBindList().stream()
              .map(binding -> new BindingConfigProxy(binding, xmlServerConfiguration))
              .collect(Collectors.toCollection(ArrayList<IBinding>::new));
    }

    @Override
    public List<IBinding> getBindings() {
      return this._bindings;
    }
  }

  private static class CommandsConfigProxy implements ICommandsConfig {
    private final List<ICommandConfig> _commandDefinitionList;

    private CommandsConfigProxy(XMLCommands xmlCommands) {
      this._commandDefinitionList =
          xmlCommands.getCommandList().stream()
              .map(CommandDefinitionProxy::new)
              .collect(Collectors.toCollection(ArrayList<ICommandConfig>::new));
    }

    @Override
    public List<ICommandConfig> getDefinitions() {
      return _commandDefinitionList;
    }
  }

  private static class CommandDefinitionProxy implements ICommandConfig {

    private final XMLCommand _xmlCommandDefinition;
    private final String _args;

    private CommandDefinitionProxy(XMLCommand xmlCommand) {
      this._xmlCommandDefinition = xmlCommand;

      this._args =
          xmlCommand.getArgList().stream()
              .map(
                  arg ->
                      String.format(
                          "%s%s %s",
                          arg.getName().length() == 1 ? "-" : "--", arg.getName(), arg.getValue()))
              .reduce("", (acc, arg) -> String.join(" ", acc, arg))
              .trim();
    }

    @Override
    public String getName() {
      return _xmlCommandDefinition.getName();
    }

    @Override
    public String getPlugin() {
      return _xmlCommandDefinition.getPluginName();
    }

    @Override
    public String getArgs() {
      return _args;
    }
  }

  public XMLJNRPEConfigurationProxy(XMLConfiguration xmlConf) {
    this._serverConfig = new ServerConfigProxy(xmlConf.getServerConfig());
    this._commandsConfig = new CommandsConfigProxy(xmlConf.getCommands());
  }

  @Override
  public IServerConfig getServer() {
    return _serverConfig;
  }

  @Override
  public ICommandsConfig getCommands() {
    return _commandsConfig;
  }
}
