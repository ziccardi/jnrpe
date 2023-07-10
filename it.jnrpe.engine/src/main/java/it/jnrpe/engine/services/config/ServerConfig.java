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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerConfig {
  private List<Binding> bindings = new ArrayList<>();
  ;

  public ServerConfig() {}

  ServerConfig(ServerConfig sc) {
    this.bindings = sc.bindings.stream().map(Binding::clone).collect(Collectors.toList());
  }

  public List<Binding> getBindings() {
    return Collections.unmodifiableList(bindings);
  }

  public void setBindings(List<Binding> bindings) {
    this.bindings = new ArrayList<>(bindings);
  }

  void addBinding(Binding b) {
    this.bindings.add(b);
  }

  @Override
  public String toString() {
    return "ServerConfig{" + "bindings=" + bindings + '}';
  }
}
