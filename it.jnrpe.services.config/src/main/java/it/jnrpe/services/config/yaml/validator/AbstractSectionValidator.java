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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSectionValidator implements IConfigSectionValidator {
  private final List<String> validSections;
  private final List<String> mandatorySections;

  protected AbstractSectionValidator(String[] validSections, String[] mandatorySections) {
    this.validSections = Arrays.asList(validSections);
    this.mandatorySections = Arrays.asList(mandatorySections);
  }

  @Override
  public void validate(Object section) throws InvalidConfigurationException {
    if (section instanceof Map) {
      var data = (Map<String, Object>) section;
      var invalidKeys =
          data.keySet().stream()
              .filter(s -> !validSections.contains(s))
              .collect(Collectors.toList());
      if (!invalidKeys.isEmpty()) {
        throw new InvalidConfigurationException("Unknown section(s) found: " + invalidKeys);
      }
      List<String> missingMandatoryKeys =
          this.mandatorySections.stream()
              .filter(s -> !data.containsKey(s))
              .collect(Collectors.toList());
      if (!missingMandatoryKeys.isEmpty()) {
        throw new InvalidConfigurationException(
            "Missing mandatory key(s): " + missingMandatoryKeys);
      }
    } else {
      throw new InvalidConfigurationException(
          "Bad input received: " + section + " expected a map<string, object>");
    }
  }
}
