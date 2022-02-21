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
package it.jnrpe.services.config.yaml.validator;

import it.jnrpe.services.config.yaml.IConfigSectionValidator;
import java.util.List;
import java.util.Map;

public class BindingsConfigValidator implements IConfigSectionValidator {

  private static class BindingConfigValidator extends AbstractSectionValidator {
    protected BindingConfigValidator() {
      super(new String[] {"ip", "port", "ssl", "allow"}, new String[] {"ip", "port"});
    }
  }

  public BindingsConfigValidator() {}

  @Override
  public void validate(Object section) throws InvalidConfigurationException {
    if (!(section instanceof List)) {
      throw new InvalidConfigurationException("Bad input received: " + section);
    }

    BindingConfigValidator bcv = new BindingConfigValidator();

    var data = (List<Map<String, Object>>) section;

    for (int i = 0; i < data.size(); i++) {
      try {
        bcv.validate(data.get(i));
      } catch (InvalidConfigurationException ice) {
        throw new InvalidConfigurationException("[" + i + "] " + ice.getMessage());
      }
    }
  }
}
