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
import java.util.Collections;
import java.util.List;

public class YamlJNRPEConfigProxy implements IJNRPEConfig {

  private ServerConfig serverConfig;
  private List<CommandConfig> commandsConfig;

  private void loadServerConfig(YAMLJNRPEConfig conf) {
    this.serverConfig =
        new ServerConfig(
            conf.getServer().getBindings().stream()
                .map(b -> new Binding(b.getIp(), b.getPort(), b.isSsl(), b.getAllow()))
                .toList());
  }

  private void loadCommandsConfig(YAMLJNRPEConfig conf) {
    this.commandsConfig =
        conf.getCommands().getDefinitions().stream()
            .map(c -> new CommandConfig(c.getName(), c.getPlugin(), c.getArgs()))
            .toList();
  }

  public YamlJNRPEConfigProxy(YAMLJNRPEConfig conf) {
    loadCommandsConfig(conf);
    loadServerConfig(conf);
  }

  @Override
  public ServerConfig getServer() {
    return this.serverConfig;
  }

  @Override
  public List<CommandConfig> getCommands() {
    return Collections.unmodifiableList(this.commandsConfig);
  }
}
