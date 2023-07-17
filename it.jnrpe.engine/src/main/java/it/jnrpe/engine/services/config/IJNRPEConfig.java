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
package it.jnrpe.engine.services.config;

import java.util.ArrayList;
import java.util.List;

public interface IJNRPEConfig {
  ServerConfig getServer();

  List<CommandConfig> getCommands();

  record Binding(String ip, int port, boolean ssl, List<String> allow) {
    public Binding(String ip, int port, boolean ssl, List<String> allow) {
      this.ip = ip;
      this.port = port;
      this.ssl = ssl;
      // Create a defensive copy of the allow list
      this.allow = allow != null ? List.copyOf(allow) : List.copyOf(new ArrayList<>());
    }
  }

  record CommandConfig(String name, String plugin, String args) {}

  record ServerConfig(List<Binding> bindings) {
    public ServerConfig(List<Binding> bindings) {
      // Initialize the fields (defensive copy)
      this.bindings = List.copyOf(bindings);
    }
  }
}
