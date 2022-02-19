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
package it.jnrpe.services.config.yaml;

import it.jnrpe.services.config.yaml.validator.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigValidator {
  private Map<String, IConfigSectionValidator> validators = new HashMap<>();

  public ConfigValidator() {
    validators.put("", new RootConfigValidator());
    validators.put("/server", new ServerConfigValidator());
    validators.put("/server/bindings", new BindingsConfigValidator());
    validators.put("/commands", new CommandsValidator());
    validators.put("/commands/definitions", new CommandDefinitionValidator());
  }

  private void validate(final String path, Object dataToValidate)
      throws InvalidConfigurationException {
    Object current = dataToValidate;

    for (var key : path.split("/")) {
      // TODO check that current is a map
      if (key.isEmpty()) {
        break;
      }
      current = ((Map<?, ?>) current).get(key);
    }

    var validator = validators.get(path);
    if (validator == null) {
      throw new InvalidConfigurationException("unknown section " + path);
    }

    try {
      validator.validate(current);
    } catch (InvalidConfigurationException ice) {
      throw new InvalidConfigurationException(
          "[" + (path.isEmpty() ? "/" : "") + path + "] " + ice.getMessage());
    }

    if (current instanceof Map) {
      Map<String, Object> data = (Map<String, Object>) current;
      for (Map.Entry<String, Object> entry : data.entrySet()) {
        String newPath = String.join("/", path, entry.getKey());
        validate(newPath, entry.getValue());
      }
    }
  }

  public void validate(Map<String, Object> data) throws InvalidConfigurationException {
    validate("", data);
  }
}
