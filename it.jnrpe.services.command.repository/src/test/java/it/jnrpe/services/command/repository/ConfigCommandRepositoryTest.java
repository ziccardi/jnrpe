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
package it.jnrpe.services.command.repository;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandInstance;
import it.jnrpe.engine.services.commands.ICommandRepository;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigCommandRepositoryTest {
  private final ICommandRepository commandRepository = new ConfigCommandRepository();

  @BeforeEach
  public void setUp() throws Exception {
    Field commandsField = ConfigCommandRepository.class.getDeclaredField("commandDefinitions");
    commandsField.setAccessible(true);

    commandsField.set(commandRepository, new HashMap<String, ICommandDefinition>());
  }

  private ICommandDefinition genTestCommandDefinition(String commandName) {
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

  private void addTestCommand(
      final ICommandRepository commandRepository, ICommandDefinition commandDefinition)
      throws Exception {
    // Access the private commandDefinition list field...
    Field commandsField = ConfigCommandRepository.class.getDeclaredField("commandDefinitions");
    commandsField.setAccessible(true);
    var commandsMap = (Map<String, ICommandDefinition>) commandsField.get(commandRepository);

    // Add the plugins to the repository
    commandsMap.put(commandDefinition.getName(), commandDefinition);
  }

  @Test
  public void testGetAllCommands() throws Exception {
    assertEquals(0, commandRepository.getAllCommands().size());
    // Add some commands to the repository
    ICommandDefinition command1 = genTestCommandDefinition("command1");
    ICommandDefinition command2 = genTestCommandDefinition("command2");
    addTestCommand(commandRepository, command1);
    addTestCommand(commandRepository, command2);
    assertEquals(2, commandRepository.getAllCommands().size());
  }

  @Test
  public void testGetCommand() throws Exception {
    // Add a command to the repository
    ICommandDefinition command = genTestCommandDefinition("command");
    addTestCommand(commandRepository, command);
    // Test positive case
    assertTrue(commandRepository.getCommand("command").isPresent());
    assertEquals(command, commandRepository.getCommand("command").get());
    // Test negative case
    assertFalse(commandRepository.getCommand("nonexistent").isPresent());
  }
}
