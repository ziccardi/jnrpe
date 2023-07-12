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
package it.jnrpe.engine.commands;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.commands.ICommandInitializer;
import it.jnrpe.engine.services.commands.ICommandInstance;
import it.jnrpe.engine.services.commands.ICommandRepository;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandRepositoryTest {
  private final ICommandRepository commandRepository = CommandRepository.getInstance();

  @BeforeEach
  public void setUp() throws Exception {
    Field commandsField = CommandRepository.class.getDeclaredField("commands");
    commandsField.setAccessible(true);

    commandsField.set(commandRepository, new HashMap<String, ICommandInitializer>());
  }

  private ICommandInitializer genTestCommandDefinition(String commandName) {
    return new ICommandInitializer() {
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
      final ICommandRepository commandRepository, ICommandInitializer commandDefinition)
      throws Exception {
    // Access the private commandDefinition list field...
    Field commandsField = CommandRepository.class.getDeclaredField("commands");
    commandsField.setAccessible(true);
    var commandsMap = (Map<String, ICommandInitializer>) commandsField.get(commandRepository);

    // Add the plugins to the repository
    commandsMap.put(commandDefinition.getName(), commandDefinition);
  }

  @Test
  public void testGetAllCommands() throws Exception {
    assertEquals(0, commandRepository.getAllCommands().size());
    // Add some commands to the repository
    ICommandInitializer command1 = genTestCommandDefinition("command1");
    ICommandInitializer command2 = genTestCommandDefinition("command2");
    addTestCommand(commandRepository, command1);
    addTestCommand(commandRepository, command2);
    assertEquals(2, commandRepository.getAllCommands().size());
  }

  @Test
  public void testGetCommand() throws Exception {
    // Add a command to the repository
    ICommandInitializer command = genTestCommandDefinition("command");
    addTestCommand(commandRepository, command);
    // Test positive case
    assertTrue(commandRepository.getCommand("command").isPresent());
    assertEquals(command, commandRepository.getCommand("command").get());
    // Test negative case
    assertFalse(commandRepository.getCommand("nonexistent").isPresent());
  }

  @Test
  public void testGetInstance() {
    // Ensure the instance is the same every time
    assertSame(commandRepository, CommandRepository.getInstance());
  }
}
