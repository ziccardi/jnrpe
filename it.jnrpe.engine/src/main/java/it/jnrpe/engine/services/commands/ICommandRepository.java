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

import java.util.*;

/**
 * The interface for a command repository.
 *
 * <p>This interface provides methods for getting commands and all commands.
 */
public interface ICommandRepository {

  /**
   * Gets a list of all command repositories.
   *
   * @return A list of all command repositories.
   */
  static List<ICommandRepository> getProviders() {
    ServiceLoader<ICommandRepository> services = ServiceLoader.load(ICommandRepository.class);
    return services.stream().map(ServiceLoader.Provider::get).toList();
  }

  /**
   * Gets a command by name.
   *
   * @param commandName The name of the command.
   * @return The command, if it exists.
   */
  Optional<ICommandFactory> getCommand(String commandName);

  /**
   * Gets all commands.
   *
   * @return A collection of all commands.
   */
  Collection<ICommandFactory> getAllCommands();
}
