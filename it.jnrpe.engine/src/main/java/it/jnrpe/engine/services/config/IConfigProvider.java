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
package it.jnrpe.engine.services.config;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * The interface for a configuration provider.
 *
 * <p>This interface provides methods for getting the configuration and generating a sample
 * configuration.
 */
public interface IConfigProvider {
  /**
   * Gets a list of all configuration providers.
   *
   * @return A list of all configuration providers.
   */
  static List<IConfigProvider> getProviders() {
    return ServiceLoader.load(IConfigProvider.class).stream()
        .map(ServiceLoader.Provider::get)
        .collect(Collectors.toList());
  }

  /**
   * Gets the name of the configuration provider.
   *
   * @return The name of the configuration provider.
   */
  String getProviderName();

  /**
   * Gets the configuration.
   *
   * @return The configuration, if it exists.
   */
  Optional<IJNRPEConfig> getConfig();

  /**
   * Generates a sample configuration.
   *
   * @return A sample configuration.
   */
  String generateSampleConfig();
}
