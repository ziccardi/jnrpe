/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.server;

import it.jnrpe.engine.services.config.IConfigSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigSource implements IConfigSource {
  private static File configFile;

  public static void setConfigFile(File confFile) {
    ConfigSource.configFile = confFile;
  }

  @Override
  public InputStream getConfigStream() throws IOException {
    return Files.newInputStream(configFile.toPath());
  }

  @Override
  public String getConfigType() {
    final String fileName = configFile.getName().toLowerCase();
    if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
      return "YAML";
    }
    if (fileName.endsWith(".xml")) {
      return "XML";
    }
    if (fileName.endsWith(".ini")) {
      return "INI";
    }

    return "UNKNOWN";
  }
}
