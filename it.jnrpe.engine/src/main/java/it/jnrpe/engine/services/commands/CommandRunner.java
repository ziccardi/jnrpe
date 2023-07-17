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
import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.auth.IAuthService;
import it.jnrpe.engine.services.network.Status;
import java.util.Optional;

/**
 * The class that execute a given command.
 *
 * <p>This class is responsible for executing commands, given a token and a command name.
 */
public class CommandRunner {
  private final ICommandRepository commandRepository;

  // TODO: implement a way to activate a different auth service
  private final IAuthService authService;

  public CommandRunner() {
    commandRepository = CommandRepository.getInstance();
    authService = IAuthService.getProviders().stream().findFirst().orElseThrow();
  }

  /**
   * Creates a new CommandRunner instance.
   *
   * @param commandRepository The command repository.
   * @param authService The authentication service.
   */
  CommandRunner(ICommandRepository commandRepository, IAuthService authService) {
    this.commandRepository = commandRepository;
    this.authService = authService;
  }

  /**
   * Executes a command.
   *
   * @param token The token to use for authentication.
   * @param commandName The name of the command to execute.
   * @param params The parameters for the command.
   * @return The result of executing the command.
   */
  public ExecutionResult execute(String token, String commandName, String... params) {
    if (!authService.authorize(token)) {
      return new ExecutionResult(String.format("Unauthorised [%s]", commandName), Status.UNKNOWN);
    }
    final Optional<ICommandFactory> command = commandRepository.getCommand(commandName);

    if (command.isPresent()) {
      return command.get().instantiate(params).execute();
    }

    EventManager.warn("Unknown command [%s]", commandName);
    return ExecutionResult.errorExecutingCommand(commandName);
  }
}
