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
package it.jnrpe.services.config.xml.validator;

import it.jnrpe.services.config.InvalidConfigurationException;
import it.jnrpe.services.config.xml.IXMLConfigSectionValidator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractXMLSectionValidator implements IXMLConfigSectionValidator {
  private final List<String> validAttributes;
  private final List<String> mandatoryAttributes;

  private final int minOccurrenciesCount;
  private final int maxOccurrenciesCount;

  private int occurrenciesCount = 0;

  protected AbstractXMLSectionValidator(String[] validAttributes, String[] mandatoryAttributes) {
    this.validAttributes = Arrays.asList(validAttributes);
    this.mandatoryAttributes = Arrays.asList(mandatoryAttributes);
    this.minOccurrenciesCount = 0;
    this.maxOccurrenciesCount = Integer.MAX_VALUE;
  }

  protected AbstractXMLSectionValidator(
      String[] validAttributes,
      String[] mandatoryAttributes,
      int minOccurrenciesCount,
      int maxOccurrenciesCount) {
    this.validAttributes = Arrays.asList(validAttributes);
    this.mandatoryAttributes = Arrays.asList(mandatoryAttributes);
    this.minOccurrenciesCount = minOccurrenciesCount;
    this.maxOccurrenciesCount = maxOccurrenciesCount;
  }

  @Override
  public void validate(Node xmlNode) throws InvalidConfigurationException {
    this.occurrenciesCount++;

    if (this.occurrenciesCount > this.maxOccurrenciesCount) {
      throw new InvalidConfigurationException(
          "Too many occurrences of [%s] found. Maximum allowed is %d while the current count is %d",
          xmlNode.getNodeName(), this.maxOccurrenciesCount, this.occurrenciesCount);
    }

    if (!(xmlNode instanceof Element xmlElement)) {
      throw new InvalidConfigurationException("Invalid node found: [%s]", xmlNode.getNodeName());
    }

    // check invalid attributes
    var invalidAttibutes = new ArrayList<String>();
    var attributes = xmlNode.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      var attribute = attributes.item(i);
      if (!this.validAttributes.contains(attribute.getNodeName())) {
        invalidAttibutes.add(attribute.getNodeName());
      }
    }

    if (!invalidAttibutes.isEmpty()) {
      throw new InvalidConfigurationException(
          String.format("Unknown attribute(s) found: [%s]", String.join(",", invalidAttibutes)));
    }

    var missingMandatoryAttributes =
        this.mandatoryAttributes.stream()
            .filter(att -> xmlElement.getAttribute(att).length() == 0)
            .toList();
    if (!missingMandatoryAttributes.isEmpty()) {
      throw new InvalidConfigurationException(
          String.format(
              "Missing or empty mandatory key(s): [%s]",
              String.join(",", missingMandatoryAttributes)));
    }
  }

  public void checkConstraints() throws InvalidConfigurationException {
    if (occurrenciesCount > maxOccurrenciesCount) {
      throw new InvalidConfigurationException(
          "Too many occurrences found. Maximum allowed is %d while the current count is %d",
          this.maxOccurrenciesCount, this.occurrenciesCount);
    }
    if (occurrenciesCount < minOccurrenciesCount) {
      throw new InvalidConfigurationException(
          "No enough occurrences found. Minimum allowed is %d while the current count is %d",
          this.minOccurrenciesCount, this.occurrenciesCount);
    }
  }
}
