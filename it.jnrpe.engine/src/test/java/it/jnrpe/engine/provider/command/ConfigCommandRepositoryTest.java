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
package it.jnrpe.engine.provider.command;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandInstance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public class ConfigCommandRepositoryTest {
  private ConfigCommandRepository getConfigCommandRepository() {
    return new ConfigCommandRepository();
  }

  private ConfigCommandRepository getConfigCommandRepository(
      Consumer<Map<String, ICommandDefinition>> customizer) {
    return new ConfigCommandRepository(customizer);
  }

  @Test
  public void testGetAllCommands() {
    assertNotNull(getConfigCommandRepository().getAllCommands());
    assertFalse(
        getConfigCommandRepository(
                repo -> {
                  repo.put("command1", createCommand("command1"));
                })
            .getAllCommands()
            .isEmpty());
    assertTrue(getConfigCommandRepository().getAllCommands().isEmpty());
  }

  @Test
  public void testGetCommand() {
    var commandRepository =
        getConfigCommandRepository(
            repo -> {
              repo.put("command1", createCommand("command1"));
            });
    String commandName = "command1";
    Optional<ICommandDefinition> commandDefinition = commandRepository.getCommand(commandName);
    assertTrue(commandDefinition.isPresent());
    assertEquals(commandName, commandDefinition.get().getName());
  }

  @Test
  public void testGetCommandNotFound() {
    var commandRepository = getConfigCommandRepository();
    String commandName = "nonExistingCommand";
    Optional<ICommandDefinition> commandDefinition = commandRepository.getCommand(commandName);
    assertFalse(commandDefinition.isPresent());
  }

  private ICommandDefinition createCommand(String commandName) {
    return new ICommandDefinition() {
      @Override
      public String getName() {
        return commandName;
      }

      @Override
      public ICommandInstance instantiate(String... params) {
        return null;
      }
    };
  }
}
