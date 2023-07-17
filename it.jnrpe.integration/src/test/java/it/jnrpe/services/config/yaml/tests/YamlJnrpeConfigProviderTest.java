/*******************************************************************************
 * Copyright (C) 2022, Massimiliano Ziccardi
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
package it.jnrpe.services.config.yaml.tests;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.config.ConfigurationManager;
import org.junit.jupiter.api.Test;

public class YamlJnrpeConfigProviderTest {

  @Test
  public void testValidYamlConfig() {
    var config = ConfigurationManager.getConfig().orElseThrow();
    var bindings = config.getServer().bindings();
    assertEquals(3, bindings.size());
    var binding = bindings.get(0);
    assertEquals("127.0.0.1", binding.ip());
    assertEquals(5667, binding.port());
    assertFalse(binding.ssl());

    binding = bindings.get(1);
    assertEquals("127.0.0.1", binding.ip());
    assertEquals(5668, binding.port());
    assertFalse(binding.ssl());

    binding = bindings.get(2);
    assertEquals("127.0.0.1", binding.ip());
    assertEquals(5669, binding.port());
    assertTrue(binding.ssl());

    var commandsConfig = config.getCommands();
    assertNotNull(commandsConfig);
    assertEquals(4, commandsConfig.size());
    var commandDefinition = commandsConfig.get(0);
    assertEquals("CMD_TEST", commandDefinition.name());
    assertEquals("PLUGIN_TEST", commandDefinition.plugin());
    assertEquals("-a 1 -b 2 -c 3", commandDefinition.args());
  }
}
