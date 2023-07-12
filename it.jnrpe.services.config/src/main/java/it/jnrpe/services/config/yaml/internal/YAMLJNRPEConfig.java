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
package it.jnrpe.services.config.yaml.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YAMLJNRPEConfig {
  private ServerConfig server;
  private CommandsConfig commands;

  public static class ServerConfig {
    public List<Binding> bindings = new ArrayList<>();

    public ServerConfig() {}

    public List<Binding> getBindings() {
      return Collections.unmodifiableList(bindings);
    }

    @Override
    public String toString() {
      return "ServerConfig{" + "bindings=" + bindings + '}';
    }
  }

  public static class Binding {
    private String ip;
    private int port;
    private boolean ssl;
    private List<String> allow = new ArrayList<>();

    public Binding() {}

    public Binding(String ip, int port, boolean ssl) {
      this.ip = ip;
      this.port = port;
      this.ssl = ssl;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getIp() {
      return ip;
    }

    public void setIp(String ipAddress) {
      this.ip = ipAddress;
    }

    public boolean isSsl() {
      return ssl;
    }

    public void setSsl(boolean ssl) {
      this.ssl = ssl;
    }

    public List<String> getAllow() {
      return Collections.unmodifiableList(allow);
    }

    public void setAllow(List<String> allow) {
      this.allow = new ArrayList<>(allow);
    }

    public void addAllowedAddress(String ip) {
      this.allow.add(ip);
    }

    @Override
    public String toString() {
      return "BindingConfigProxy{"
          + "ip='"
          + ip
          + '\''
          + ", port="
          + port
          + ", ssl="
          + ssl
          + ", allow="
          + allow
          + '}';
    }
  }

  public static class CommandsConfig {
    private List<CommandDefinition> definitions = new ArrayList<>();

    public List<CommandDefinition> getDefinitions() {
      return Collections.unmodifiableList(definitions);
    }

    public void setDefinitions(List<CommandDefinition> definitions) {
      this.definitions = new ArrayList<>(definitions);
    }

    @Override
    public String toString() {
      return "CommandsConfig{" + "definitions=" + definitions + '}';
    }
  }

  public static class CommandDefinition {
    private String name;
    private String plugin;
    private String args;

    public CommandDefinition() {}

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
    public String toString() {
      return "CommandInitializer{"
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

  public YAMLJNRPEConfig() {}

  public ServerConfig getServer() {
    return this.server;
  }

  public void setServer(ServerConfig server) {
    this.server = server;
  }

  @Override
  public String toString() {
    return "YAMLJNRPEConfig{" + "server=" + server + ", commands=" + commands + '}';
  }

  public CommandsConfig getCommands() {
    return this.commands;
  }

  public void setCommands(CommandsConfig commands) {
    this.commands = commands;
  }
}
