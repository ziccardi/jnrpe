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
import java.util.List;

public class XMLJNRPEConfigurationProxy implements IJNRPEConfig {
  private ServerConfig serverConfig;
  private CommandsConfig commandsConfig;
  private static final int DEFAULT_BINDING_PORT = 5667;

  private void loadServerConfig(XMLConfiguration conf) {
    List<Binding> bindings =
        conf.getServerConfig().getBindList().stream()
            .map(
                b ->
                    new Binding(
                        b.getHost(),
                        b.getPort(),
                        b.isSsl(),
                        conf.getServerConfig().getAllowList().stream()
                            .map(XMLAllow::getIp)
                            .toList()))
            .toList();
    this.serverConfig = new ServerConfig(bindings);
  }

  private void loadCommandsConfig(XMLConfiguration conf) {
    this.commandsConfig =
        new CommandsConfig(
            conf.getCommands().getCommandList().stream()
                .map(
                    c ->
                        new CommandConfig(
                            c.getName(),
                            c.getPluginName(),
                            c.getArgList().stream()
                                .map(
                                    arg ->
                                        String.format(
                                                "%s%s %s",
                                                arg.getName().length() == 1 ? "-" : "--",
                                                arg.getName(),
                                                arg.getValue())
                                            .trim())
                                .reduce("", (acc, arg) -> String.join(" ", acc, arg))
                                .trim()))
                .toList());
  }

  public XMLJNRPEConfigurationProxy(XMLConfiguration xmlConf) {
    this.loadServerConfig(xmlConf);
    this.loadCommandsConfig(xmlConf);
  }

  @Override
  public ServerConfig getServer() {
    return serverConfig;
  }

  @Override
  public CommandsConfig getCommands() {
    return commandsConfig;
  }
}
