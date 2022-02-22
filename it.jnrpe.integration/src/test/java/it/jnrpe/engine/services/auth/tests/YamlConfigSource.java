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
        "--- \n"
            + "server: \n"
            + "  bindings: \n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5667\n"
            + "      ssl: false\n"
            + "      allow: \n"
            + "        - \"127.0.0.2\" \n"
            + "        - \"127.0.0.3\" \n"
            + "        - \"127.0.0.4\" \n"
            + "    - \n"
            + "      ip: \"127.0.0.1\"\n"
            + "      port: 5668\n"
            + "      ssl: false\n"
            + "commands: \n"
            + "  definitions: \n"
            + "    - \n"
            + "      args: \"-a 1 -b 2 -c 3\"\n"
            + "      name: CMD_TEST\n"
            + "      plugin: PLUGIN_TEST";

    return new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
  }
}
