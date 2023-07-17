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
import it.jnrpe.engine.services.commands.ICommandFactory;
import it.jnrpe.engine.services.commands.ICommandRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link CommandRepository} class is a singleton responsible for managing and storing all
 * available commands in the application. The list of commands is obtained by querying all the
 * available implementations provided by the installed service providers. It implements the {@link
 * ICommandRepository} interface.
 *
 * <p>The {@link CommandRepository} also logs an info message when it is ready, indicating the
 * number of commands loaded.
 */
public class CommandRepository implements ICommandRepository {

  private static CommandRepository INSTANCE;

  private final Map<String, ICommandFactory> commands = new HashMap<>();

  private CommandRepository() {
    ICommandRepository.getProviders()
        .forEach(
            commandRepository -> {
              commandRepository
                  .getAllCommands()
                  .forEach(command -> commands.put(command.getName(), command));
            });

    EventManager.info("Command Repository ready. %d command(s) loaded", this.commands.size());
  }

  /**
   * Retrieves all command factories.
   *
   * @return A collection of {@link ICommandFactory} objects.
   */
  @Override
  public Collection<ICommandFactory> getAllCommands() {
    return commands.values();
  }

  /**
   * Retrieves the command factory associated with the given command name.
   *
   * @param commandName The name of the command.
   * @return An Optional containing the {@link ICommandFactory} for the specified command name, or
   *     an empty {@link Optional} if the command is not found.
   */
  @Override
  public Optional<ICommandFactory> getCommand(String commandName) {
    return Optional.ofNullable(commands.get(commandName));
  }

  public static synchronized ICommandRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CommandRepository();
    }

    return INSTANCE;
  }
}
