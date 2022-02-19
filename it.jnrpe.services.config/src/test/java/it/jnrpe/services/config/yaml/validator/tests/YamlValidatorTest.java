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
package it.jnrpe.services.config.yaml.validator.tests;

import it.jnrpe.services.config.yaml.ConfigValidator;
import it.jnrpe.services.config.yaml.validator.InvalidConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class YamlValidatorTest {

  @Test
  public void testInvalidSingleRootKey() throws Exception {
    String yamlText =
        "--- \n"
            + "server: \n"
            + "  bindings: \n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5667\n"
            + "      ssl: false\n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5668\n"
            + "      ssl: false\n"
            + "invalid: \n"
            + "  definitions: \n"
            + "    - \n"
            + "      args: \"-a 1 -b 2 -c 3\"\n"
            + "      name: CMD_TEST\n"
            + "      plugin: PLUGIN_TEST";
    Yaml yaml = new Yaml();
    try {
      new ConfigValidator().validate(yaml.load(yamlText));
    } catch (InvalidConfigurationException ice) {
      Assertions.assertEquals("[/] Unknown section(s) found: [invalid]", ice.getMessage());
    }
  }

  @Test
  public void testInvalidMultipleRootKey() throws Exception {
    String yamlText =
        "--- \n"
            + "server: \n"
            + "  bindings: \n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5667\n"
            + "      ssl: false\n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5668\n"
            + "      ssl: false\n"
            + "invalid: \n"
            + "  definitions: \n"
            + "    - \n"
            + "      args: \"-a 1 -b 2 -c 3\"\n"
            + "      name: CMD_TEST\n"
            + "      plugin: PLUGIN_TEST\n"
            + "invalid2: \n"
            + "  definitions: \n"
            + "    - \n"
            + "      args: \"-a 1 -b 2 -c 3\"\n"
            + "      name: CMD_TEST\n"
            + "      plugin: PLUGIN_TEST";
    Yaml yaml = new Yaml();
    try {
      new ConfigValidator().validate(yaml.load(yamlText));
    } catch (InvalidConfigurationException ice) {
      Assertions.assertEquals(
          "[/] Unknown section(s) found: [invalid, invalid2]", ice.getMessage());
    }
  }

  @Test
  public void testMandatoryRootKeyMissing() throws Exception {
    String yamlText =
        "--- \n"
            + "commands: \n"
            + "  definitions: \n"
            + "    - \n"
            + "      args: \"-a 1 -b 2 -c 3\"\n"
            + "      name: CMD_TEST\n"
            + "      plugin: PLUGIN_TEST";
    Yaml yaml = new Yaml();
    try {
      new ConfigValidator().validate(yaml.load(yamlText));
    } catch (InvalidConfigurationException ice) {
      Assertions.assertEquals("[/] Missing mandatory key(s): [server]", ice.getMessage());
    }
  }

  @Test
  public void testMissingNestedMandatoryKey() throws Exception {
    String yamlText =
        "--- \n"
            + "server: \n"
            + "  bindings: \n"
            + "    - \n"
            + "      port: 5667\n"
            + "      ssl: false\n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5668\n"
            + "      ssl: false\n";
    Yaml yaml = new Yaml();
    try {
      new ConfigValidator().validate(yaml.load(yamlText));
    } catch (InvalidConfigurationException ice) {
      Assertions.assertEquals(
          "[/server/bindings] [0] Missing mandatory key(s): [ip]", ice.getMessage());
    }
  }

  @Test
  public void testInvalidNestedKey() throws Exception {
    String yamlText =
        "--- \n"
            + "server: \n"
            + "  bindings: \n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5667\n"
            + "      ssl: false\n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5668\n"
            + "      bad: 123\n"
            + "      ssl: false\n";
    Yaml yaml = new Yaml();
    try {
      new ConfigValidator().validate(yaml.load(yamlText));
    } catch (InvalidConfigurationException ice) {
      Assertions.assertEquals(
          "[/server/bindings] [1] Unknown section(s) found: [bad]", ice.getMessage());
    }
  }
}
