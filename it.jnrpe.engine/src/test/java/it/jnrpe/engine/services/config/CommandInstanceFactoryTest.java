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
package it.jnrpe.engine.services.config;

import static it.jnrpe.engine.services.config.IJNRPEConfig.CommandConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.jnrpe.engine.services.commands.TestPlugin;
import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandInstanceFactoryTest {
  private IPluginRepository pluginRespository;

  @BeforeEach
  public void setUp() {
    this.pluginRespository = mock(IPluginRepository.class);
  }

  @Test
  public void testExecuteWithParams() {
    var testPlugin = new TestPlugin();

    assertNull(testPlugin.option1);
    assertNull(testPlugin.option2);
    assertNull(testPlugin.option3);
    assertNull(testPlugin.option4);

    when(pluginRespository.getPlugin("test")).thenReturn(Optional.of(testPlugin));

    var commandConfig =
        new CommandConfig(
            "UNIT_TEST_PLUGIN", "test", "-a $ARG1$ -b fixedParam1 -c $aRg1$ -d $ARG2$");
    var commandFactory = new CommandInstanceFactory(this.pluginRespository, commandConfig);
    var instance = commandFactory.instantiate("arg1", "arg2");
    var result = instance.execute();

    assertEquals("arg1", testPlugin.option1);
    assertEquals("fixedParam1", testPlugin.option2);
    assertEquals("arg1", testPlugin.option3);
    assertEquals("arg2", testPlugin.option4);
    assertEquals(Status.OK, result.getStatus());
  }

  @Test
  public void testMissingMandatory() {
    var testPlugin = new TestPlugin();

    assertNull(testPlugin.option1);
    assertNull(testPlugin.option2);
    assertNull(testPlugin.option3);
    assertNull(testPlugin.option4);

    when(pluginRespository.getPlugin("test")).thenReturn(Optional.of(testPlugin));

    var commandConfig =
        new CommandConfig("UNIT_TEST_COMMAND", "test", "-b fixedParam1 -c $aRg1$ -d $ARG2$");
    var commandFactory = new CommandInstanceFactory(this.pluginRespository, commandConfig);
    var instance = commandFactory.instantiate("arg1", "arg2");
    var result = instance.execute();

    assertEquals(Status.UNKNOWN, result.getStatus());
    assertEquals("[UNIT_TEST_COMMAND - UNKNOWN] - Error executing command", result.getMessage());
  }

  @Test
  public void testArgsWithSpaces() {
    var testPlugin = new TestPlugin();

    assertNull(testPlugin.option1);
    assertNull(testPlugin.option2);
    assertNull(testPlugin.option3);
    assertNull(testPlugin.option4);

    when(pluginRespository.getPlugin("test")).thenReturn(Optional.of(testPlugin));

    var commandConfig =
        new CommandConfig(
            "UNIT_TEST_COMMAND", "test", "-a $ARG1$ -b fixedParam1 -c $aRg1$ -d $ARG2$");
    var commandFactory = new CommandInstanceFactory(this.pluginRespository, commandConfig);
    var instance = commandFactory.instantiate("arg1", "arg2 -a");
    var result = instance.execute();

    assertEquals(Status.OK, result.getStatus());

    assertEquals("arg1", testPlugin.option1);
    assertEquals("fixedParam1", testPlugin.option2);
    assertEquals("arg1", testPlugin.option3);
    assertEquals("arg2 -a", testPlugin.option4);
    assertEquals(Status.OK, result.getStatus());
  }
}
