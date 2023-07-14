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
package it.jnrpe.services.config.yaml;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.jnrpe.services.config.InvalidConfigurationException;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;

public class YamlJnrpeConfigProviderTest {
  @Test
  public void testValidConfig() throws Exception {
    String xmlText =
        """
                        server:
                          # Configure the IP and the PORT where JNRPE will listen.
                          # Use 0.0.0.0 as address to bind all addresses on the same port
                          bindings:
                            -
                              ip: "127.0.0.1"
                              port: 5666
                              ssl: false
                              allow:
                              -
                                "127.0.0.1"
                        commands:
                          definitions:
                            -
                              name: check_disk_C
                              plugin: CHECK_DISK
                              args: "--path C: --warning $ARG1$ --critical $ARG2$"
                            -
                              name: check_disk_E
                              plugin: CHECK_DISK
                              args: "--path E: --warning $ARG1$ --critical $ARG2$"
                        """;
    try (var bin = new ByteArrayInputStream(xmlText.getBytes())) {
      var conf = new YamlJnrpeConfigProvider().parseConf(bin);
      var jnrpeConf = new YamlJNRPEConfigProxy(conf);
      assertNotNull(jnrpeConf.getServer());
      assertNotNull(jnrpeConf.getCommands());
      assertNotNull(jnrpeConf.getServer().getBindings());
      assertEquals(1, jnrpeConf.getServer().getBindings().size());
      assertEquals("127.0.0.1", jnrpeConf.getServer().getBindings().get(0).getIp());
      assertEquals(5666, jnrpeConf.getServer().getBindings().get(0).getPort());
      assertFalse(jnrpeConf.getServer().getBindings().get(0).isSsl());
      assertEquals(1, jnrpeConf.getServer().getBindings().get(0).getAllow().size());
      assertEquals("127.0.0.1", jnrpeConf.getServer().getBindings().get(0).getAllow().get(0));
      assertNotNull(jnrpeConf.getCommands());
      assertEquals(2, jnrpeConf.getCommands().getDefinitions().size());
      assertEquals("check_disk_C", jnrpeConf.getCommands().getDefinitions().get(0).getName());
      assertEquals("CHECK_DISK", jnrpeConf.getCommands().getDefinitions().get(0).getPlugin());
      assertEquals(
          "--path C: --warning $ARG1$ --critical $ARG2$",
          jnrpeConf.getCommands().getDefinitions().get(0).getArgs());
      assertEquals("check_disk_E", jnrpeConf.getCommands().getDefinitions().get(1).getName());
      assertEquals("CHECK_DISK", jnrpeConf.getCommands().getDefinitions().get(1).getPlugin());
      assertEquals(
          "--path E: --warning $ARG1$ --critical $ARG2$",
          jnrpeConf.getCommands().getDefinitions().get(1).getArgs());
    }
  }

  @Test
  public void testMalformedConfig() throws Exception {
    String xmlText =
        """
            This configuration is malformed.
            server:
              # Configure the IP and the PORT where JNRPE will listen.
              # Use 0.0.0.0 as address to bind all addresses on the same port
              bindings:
                -
                  ip: "127.0.0.1"
                  port: 5666
                  ssl: false
                  allow:
                  -
                    "127.0.0.1"
            commands:
              definitions:
                -
                  name: check_disk_C
                  plugin: CHECK_DISK
                  args: "--path C: --warning $ARG1$ --critical $ARG2$"
                -
                  name: check_disk_E
                  plugin: CHECK_DISK
                  args: "--path E: --warning $ARG1$ --critical $ARG2$"
            """;
    try (var bin = new ByteArrayInputStream(xmlText.getBytes())) {
      Exception exception =
          assertThrows(
              InvalidConfigurationException.class,
              () -> new YamlJnrpeConfigProvider().parseConf(bin));
      assertTrue(exception.getMessage().startsWith("unable to parse the configuration: "));
    }
  }
}
