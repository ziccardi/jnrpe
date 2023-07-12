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

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.*;
import it.jnrpe.services.config.InvalidConfigurationException;
import it.jnrpe.services.config.yaml.internal.YAMLJNRPEConfig;
import java.io.IOException;
import java.util.Optional;
import java.util.ServiceLoader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;

public class YamlJnrpeConfigProvider implements IConfigProvider {

  private static Optional<IJNRPEConfig> config = Optional.empty();

  @Override
  public String getProviderName() {
    return "YAML";
  }

  public Optional<IJNRPEConfig> getConfig() {
    if (config.isEmpty()) {
      try {
        config = parseConfig();
      } catch (Exception e) {
        EventManager.error("Unable to parse YAML configuration: %s", e.getMessage());
      }
    }

    return config;
  }

  private Optional<IJNRPEConfig> parseConfig() throws IOException {
    // Retrieve the config source
    ServiceLoader<IConfigSource> configSourceServiceLoader =
        ServiceLoader.load(IConfigSource.class);
    var configServicesCount = configSourceServiceLoader.stream().count();

    if (configServicesCount > 1) {
      EventManager.warn(
          "More than one config service has been found [%d]. Only first one will be used",
          configServicesCount);
      return Optional.empty();
    }

    Optional<IConfigSource> optionalConfigSource = configSourceServiceLoader.findFirst();

    if (optionalConfigSource.isPresent()) {
      Yaml yaml = new Yaml();
      var yamlConfigDescription = new TypeDescription(YAMLJNRPEConfig.class, YAMLJNRPEConfig.class);
      yamlConfigDescription.addPropertyParameters("server", YAMLJNRPEConfig.ServerConfig.class);

      var serverConfigDescription =
          new TypeDescription(IServerConfig.class, YAMLJNRPEConfig.ServerConfig.class);
      // yamlConfigDescription.addPropertyParameters("bindings", ArrayList.class);
      serverConfigDescription.putListPropertyType("binding", YAMLJNRPEConfig.Binding.class);

      yaml.addTypeDescription(yamlConfigDescription);
      yaml.addTypeDescription(serverConfigDescription);
      yaml.addTypeDescription(new TypeDescription(IBinding.class, YAMLJNRPEConfig.Binding.class));
      try {
        new ConfigValidator().validate(yaml.load(optionalConfigSource.get().getConfigStream()));
      } catch (InvalidConfigurationException ice) {
        EventManager.error("YAML Config parsing error: %s", ice.getMessage());
        return Optional.empty();
      }

      return Optional.of(
          new YamlJNRPEConfigProxy(
              yaml.loadAs(optionalConfigSource.get().getConfigStream(), YAMLJNRPEConfig.class)));
    }

    EventManager.warn("No config services have been provided");
    return Optional.empty();
  }

  @Override
  public String generateSampleConfig() {
    return """
            server:
              # Configure the IP and the PORT where JNRPE will listen.
              # Use 0.0.0.0 as address to bind all addresses on the same port
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
                  plugin: CHECK_TEST
                  args: "-m 'Test Message'"
            """;
  }
}
