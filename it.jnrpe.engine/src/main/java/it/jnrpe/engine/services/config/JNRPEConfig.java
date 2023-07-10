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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class JNRPEConfig {
  private ServerConfig server;
  private CommandsConfig commands;

  public JNRPEConfig() {}

  @SafeVarargs
  public JNRPEConfig(Consumer<JNRPEConfig>... options) {
    Arrays.stream(options).forEach(o -> o.accept(this));
  }

  private class ServerConfigReadOnly extends ServerConfig {

    @Override
    public List<Binding> getBindings() {
      return server.getBindings();
    }

    @Override
    public void setBindings(List<Binding> bindings) {
      throw new IllegalStateException("Read only ServiceConfig instance");
    }
  }

  private class CommandsConfigReadOnly extends CommandsConfig {
    @Override
    public List<CommandDefinition> getDefinitions() {
      return commands.getDefinitions();
    }

    @Override
    public void setDefinitions(List<CommandDefinition> definitions) {
      throw new IllegalStateException("Read only CommandsConfig instance");
    }
  }

  public ServerConfig getServer() {
    return new ServerConfigReadOnly();
  }

  public void setServer(ServerConfig server) {
    this.server = new ServerConfig(server);
  }

  @Override
  public String toString() {
    return "JNRPEConfig{" + "server=" + server + ", commands=" + commands + '}';
  }

  public CommandsConfig getCommands() {
    return new CommandsConfigReadOnly();
  }

  public void setCommands(CommandsConfig commands) {
    this.commands = commands.clone();
  }

  public final JNRPEConfig withBinding(String address, int port, boolean ssl) {
    if (this.server == null) {
      this.server = new ServerConfig();
    }
    this.server.addBinding(new Binding(address, port, ssl));
    return this;
  }

  public final JNRPEConfig withAllowedAddress(String address, int port, String allowedIP) {
    this.server.getBindings().stream()
        .filter(b -> b.getIp().equals(address) && b.getPort() == port)
        .findFirst()
        .ifPresent(b -> b.addAllowedAddress(allowedIP));
    return this;
  }
}
