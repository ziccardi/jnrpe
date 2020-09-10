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

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.engine.services.events.LogEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandRepository implements ICommandRepository {

  private static CommandRepository INSTANCE;

  private final Map<String, ICommandDefinition> commands = new HashMap<>();

  private CommandRepository() {
    ICommandRepository.getInstances()
        .forEach(
            commandRepository -> {
              commandRepository
                  .getAllCommands()
                  .forEach(command -> commands.put(command.getName(), command));
            });

    EventManager.emit(
        LogEvent.INFO,
        String.format("Command Repository ready. %d command(s) loaded", this.commands.size()));
  }

  @Override
  public Collection<ICommandDefinition> getAllCommands() {
    return commands.values();
  }

  @Override
  public Optional<ICommandDefinition> getCommand(String commandName) {
    return Optional.ofNullable(commands.get(commandName));
  }

  public static synchronized ICommandRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CommandRepository();
    }

    return INSTANCE;
  }
}
