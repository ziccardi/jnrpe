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
package it.jnrpe.engine.services.commands;

import it.jnrpe.engine.commands.CommandRepository;
import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import java.util.Optional;

public class CommandExecutor {
  private static final ICommandRepository commandRepository = CommandRepository.getInstance();
  private IPluginRepository pluginRepository = null;

  public ExecutionResult execute(String commandName, String... params) {
    final Optional<ICommandDefinition> command = commandRepository.getCommand(commandName);

    if (command.isPresent()) {
      return command.get().instantiate(pluginRepository, params).execute();
    }

    return new ExecutionResult("Unknown command [" + commandName + ']', Status.UNKNOWN);
  }
}
