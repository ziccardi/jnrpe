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
package it.jnrpe.engine.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

  private Map<String, Command> commandEntryMap = new HashMap<>();

  static class Command {
    private final String pluginName;
    private final String[] params;

    public Command(final String pluginName, final String[] params) {
      this.pluginName = pluginName;
      this.params = Arrays.copyOf(params, params.length);
    }

    public String getPluginName() {
      return pluginName;
    }

    public String[] getParams() {
      return params;
    }
  }

  public Command getCommand(final String commandName) {
    return commandEntryMap.get(commandName);
  }
}
