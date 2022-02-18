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
package it.jnrpe.services.config.yaml;

import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.engine.services.config.JNRPEConfig;
import java.io.IOException;
import java.util.Optional;
import java.util.ServiceLoader;
import org.yaml.snakeyaml.Yaml;

public class YamlJnrpeConfigProvider implements IConfigProvider {

  private static Optional<JNRPEConfig> config = Optional.empty();

  @Override
  public String getProviderName() {
    return "YAML";
  }

  public Optional<JNRPEConfig> getConfig() {
    if (config.isEmpty()) {
      try {
        config = parseConfig();
      } catch (Exception e) {
        // TODO: log
        e.printStackTrace();
      }
    }

    return config;
  }

  public Optional<JNRPEConfig> parseConfig() throws IOException {
    // Retrieve the config source
    ServiceLoader<IConfigSource> configSourceServiceLoader =
        ServiceLoader.load(IConfigSource.class);
    Optional<IConfigSource> optionalConfigSource = configSourceServiceLoader.findFirst();

    if (optionalConfigSource.isPresent()) {
      Yaml yaml = new Yaml();
      return Optional.of(
          yaml.loadAs(optionalConfigSource.get().getConfigStream(), JNRPEConfig.class));
    }

    return Optional.empty();
  }
}
