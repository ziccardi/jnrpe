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
package it.jnrpe.engine.services.config;

import java.util.Optional;

public class ConfigurationManager {
  private ConfigurationManager() {}

  private static JNRPEConfig CONFIG;

  public static synchronized Optional<JNRPEConfig> getConfig() {
    if (CONFIG != null) {
      return Optional.of(CONFIG);
    }

    IConfigProvider.getInstances().stream()
        .map(IConfigProvider::getConfig)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .ifPresent(c -> CONFIG = c);
    return CONFIG == null ? Optional.empty() : Optional.of(CONFIG);
  }
  ;
}
