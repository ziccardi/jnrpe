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

import java.util.Optional;

public class ConfigProvider implements IConfigProvider {
  private static ConfigProvider instance;

  private IJNRPEConfig config;

  private ConfigProvider(IJNRPEConfig config) {
    this.config = config;
  }

  @Override
  public String getProviderName() {
    return instance != null ? instance.getProviderName() : "NONE";
  }

  @Override
  public Optional<IJNRPEConfig> getConfig() {
    return Optional.of(config);
  }

  @Override
  public String generateSampleConfig() {
    return instance != null ? instance.generateSampleConfig() : "";
  }

  public static ConfigProvider getInstance() {
    if (instance == null) {
      for (var confProvider : IConfigProvider.getProviders()) {
        Optional<IJNRPEConfig> conf = confProvider.getConfig();
        if (conf.isPresent()) {
          instance = new ConfigProvider(conf.get());
          break;
        }
      }
      if (instance == null) {
        instance = new ConfigProvider(null);
      }
    }
    return instance;
  }
}
