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

public class CommandsConfig implements Cloneable {
  private List<CommandDefinition> definitions = new ArrayList<>();

  public List<CommandDefinition> getDefinitions() {
    return Collections.unmodifiableList(definitions);
  }

  public void setDefinitions(List<CommandDefinition> definitions) {
    this.definitions = new ArrayList<>(definitions);
  }

  @Override
  public CommandsConfig clone() {
    try {
      CommandsConfig cc = (CommandsConfig) super.clone();
      cc.definitions = definitions.stream().map(o -> o.clone()).collect(Collectors.toList());
      return cc;
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Never happens");
    }
  }
}
