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
package it.jnrpe.services.config.yaml.validator;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.services.config.InvalidConfigurationException;
import it.jnrpe.services.config.yaml.ConfigValidator;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class YamlValidatorTest {

  @Test
  public void testInvalidSingleRootKey() {
    String yamlText =
        """
            ---
            server:
              bindings:
                -
                  ip: "127.0.0.1"
                  port: 5667
                  ssl: false
                -
                  ip: "127.0.0.1"
                  port: 5668
                  ssl: false
              invalid:
                definitions:
                  -
                    args: "-a 1 -b 2 -c 3"
                    name: CMD_TEST
                    plugin: PLUGIN_TEST
            """;
    Yaml yaml = new Yaml();
    Exception exception =
        assertThrows(
            InvalidConfigurationException.class,
            () -> {
              new ConfigValidator().validate(yaml.load(yamlText));
            });
    assertEquals("[/server] Unknown section(s) found: [invalid]", exception.getMessage());
  }

  @Test
  public void testInvalidMultipleRootKey() {
    String yamlText =
        """
            ---
            server:
              bindings:
                -
                  ip: "127.0.0.1"
                  port: 5667
                  ssl: false
                -
                  ip: "127.0.0.1"
                  port: 5668
                  ssl: false
            invalid:
              definitions:
                -
                  args: "-a 1 -b 2 -c 3"
                  name: CMD_TEST
                  plugin: PLUGIN_TEST
            invalid2:
              definitions:
                -
                  args: "-a 1 -b 2 -c 3"
                  name: CMD_TEST
                  plugin: PLUGIN_TEST
            """;
    Yaml yaml = new Yaml();
    Exception exception =
        assertThrows(
            InvalidConfigurationException.class,
            () -> {
              new ConfigValidator().validate(yaml.load(yamlText));
            });
    assertEquals("[/] Unknown section(s) found: [invalid, invalid2]", exception.getMessage());
  }

  @Test
  public void testMandatoryRootKeyMissing() {
    String yamlText =
        """
            ---
            commands:
              definitions:
                -
                  args: "-a 1 -b 2 -c 3"
                  name: CMD_TEST
                  plugin: PLUGIN_TEST
            """;

    Yaml yaml = new Yaml();
    Exception exception =
        assertThrows(
            InvalidConfigurationException.class,
            () -> {
              new ConfigValidator().validate(yaml.load(yamlText));
            });
    assertEquals("[/] Missing mandatory key(s): [server]", exception.getMessage());
  }

  @Test
  public void testMissingNestedMandatoryKey() {
    String yamlText =
        """
            server:
              bindings:
                -
                  port: 5667
                  ssl: false
                -
                  ip: "127.0.0.1"
                  port: 5668
                  ssl: false
            """;
    Yaml yaml = new Yaml();
    Exception exception =
        assertThrows(
            InvalidConfigurationException.class,
            () -> {
              new ConfigValidator().validate(yaml.load(yamlText));
            });
    assertEquals("[/server/bindings] [0] Missing mandatory key(s): [ip]", exception.getMessage());
  }

  @Test
  public void testInvalidNestedKey() {
    String yamlText =
        """
            server:
              bindings:
                -
                  ip: "127.0.0.1"
                  port: 5667
                  ssl: false
                -
                  ip: "127.0.0.1"
                  port: 5668
                  bad: 123
                  ssl: false
            """;
    Yaml yaml = new Yaml();
    Exception exception =
        assertThrows(
            InvalidConfigurationException.class,
            () -> {
              new ConfigValidator().validate(yaml.load(yamlText));
            });
    assertEquals("[/server/bindings] [1] Unknown section(s) found: [bad]", exception.getMessage());
  }

  @Test
  public void testValidConfiguration() {
    String yamlText =
        """
            server:
              bindings:
                -
                  ip: "127.0.0.1"
                  port: 5667
                  ssl: false
                -
                  ip: "127.0.0.1"
                  port: 5668
                  ssl: false
            commands:
              definitions:
                -
                  name: CMD_TEST
                  args: "-m 'Test Message'"
                  plugin: PLUGIN_TEST
            """;
    Yaml yaml = new Yaml();
    assertDoesNotThrow(
        () -> {
          new ConfigValidator().validate(yaml.load(yamlText));
        });
  }
}
