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
package it.jnrpe.engine.services.auth.tests;

import it.jnrpe.engine.services.config.IConfigSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class YamlConfigSource implements IConfigSource {
  @Override
  public String getConfigType() {
    return "YAML";
  }

  @Override
  public InputStream getConfigStream() {
    String config =
        """
            ---
            server:
              bindings:
                -
                  ip: "127.0.0.1"
                  port: 5667
                  ssl: false
                  allow:
                    - "127.0.0.2"
                    - "127.0.0.3"
                    - "127.0.0.4"
                -
                  ip: "127.0.0.1"
                  port: 5668
                  ssl: false
                -
                  ip: "127.0.0.1"
                  port: 5669
                  ssl: true
            commands:
              definitions:
                -
                  args: "-a 1 -b 2 -c 3"
                  name: CMD_TEST
                  plugin: PLUGIN_TEST
                -
                  name: CMD_TEST_SIMPLE
                  args: "-m 'This is a test'"
                  plugin: CHECK_TEST
                -
                  name: CMD_TEST_WITHARGS
                  args: "-m $ARG1$"
                  plugin: CHECK_TEST
                -
                  name: CMD_TEST_WITHARG3
                  args: "-m $ARG3$"
                  plugin: CHECK_TEST
                    """;

    return new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
  }
}
