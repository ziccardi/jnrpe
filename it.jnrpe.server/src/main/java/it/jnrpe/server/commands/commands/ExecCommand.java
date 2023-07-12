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
package it.jnrpe.server.commands.commands;

import it.jnrpe.engine.commands.CommandRepository;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.commands.ICommandInitializer;
import it.jnrpe.engine.services.plugins.CommandLine;
import java.util.List;

@CommandLine.Command(
    name = "execute",
    description = "Executes a given command",
    aliases = {"exec"})
public class ExecCommand implements Runnable {
  @CommandLine.Parameters(index = "0")
  private String commandName;

  @CommandLine.Parameters(index = "1..*")
  private List<String> commandParameters;

  private void execute(ICommandInitializer command) {
    try {
      var args =
          commandParameters == null || commandParameters.isEmpty()
              ? new String[0]
              : commandParameters.toArray(new String[0]);

      ExecutionResult res = command.instantiate(args).execute();
      System.out.printf(res.getMessage());
    } catch (Exception e) {
      System.out.println("Error executing command: " + e.getMessage());
    }
  }

  @Override
  public void run() {
    CommandRepository.getInstance()
        .getCommand(commandName)
        .ifPresentOrElse(
            this::execute,
            () -> System.out.printf("No command named [%s] has been found%n", commandName));
  }
}
