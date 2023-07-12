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
package it.jnrpe.services.config.xml;

import it.jnrpe.services.config.InvalidConfigurationException;
import it.jnrpe.services.config.xml.validator.*;
import java.util.*;
import org.w3c.dom.*;

public class ConfigValidator {
  private final Map<String, IXMLConfigSectionValidator> validators = new HashMap<>();

  public ConfigValidator() {
    // Todo: add min count
    validators.put("/#document/config", new XMLRootConfigValidator());
    validators.put("/#document/config/server", new XMLServerConfigValidator());
    validators.put("/#document/config/server/bind", new XMLBindConfigValidator());
    validators.put("/#document/config/server/allow", new XMLAllowConfigValidator());
    validators.put("/#document/config/commands", new XMLCommandsConfigValidator());
    validators.put("/#document/config/commands/command", new XMLCommandConfigValidator());
    validators.put("/#document/config/commands/command/arg", new XMLArgConfigValidator());
  }

  private List<Node> nodeListToList(NodeList nl) {
    List<Node> res = new ArrayList<>();
    for (int i = 0; i < nl.getLength(); i++) {
      res.add(nl.item(i));
    }
    return res;
  }

  private String buildPath(String currentPath, Node currentNode, Node childNode) {
    if (childNode == null) {
      return String.join("/", currentPath, currentNode.getNodeName());
    }
    return String.join("/", currentPath, currentNode.getNodeName(), childNode.getNodeName());
  }

  private void validate(final String path, Node currentNode) throws InvalidConfigurationException {

    // validate current node
    if (!(currentNode instanceof Document)) {
      var validator = validators.get(buildPath(path, currentNode, null));
      try {
        validator.validate(currentNode);
      } catch (InvalidConfigurationException e) {
        throw new InvalidConfigurationException(
            String.format("[%s] %s", buildPath(path, currentNode, null), e.getMessage()));
      }
    }

    var nodes = nodeListToList(currentNode.getChildNodes());
    // find invalid nodes
    var invalidNode =
        nodes.stream()
            .filter(node -> !(node instanceof Text))
            .filter(node -> !validators.containsKey(buildPath(path, currentNode, node)))
            .findFirst();
    if (invalidNode.isPresent()) {
      throw new InvalidConfigurationException(
          "unknown section found: [%s]", buildPath(path, currentNode, invalidNode.get()));
    }

    var error =
        nodes.stream()
            .filter(node -> !(node instanceof Text))
            .map(
                node -> {
                  try {
                    this.validate(buildPath(path, currentNode, null), node);
                    return null;
                  } catch (InvalidConfigurationException ice) {
                    return ice.getMessage();
                  }
                })
            .filter(Objects::nonNull)
            .findFirst();

    if (error.isPresent()) {
      throw new InvalidConfigurationException("%s", error.get());
    }
  }

  public void validate(Document document) throws InvalidConfigurationException {
    validate("", document);
    for (var e : this.validators.entrySet()) {
      try {
        e.getValue().checkConstraints();
      } catch (InvalidConfigurationException ice) {
        throw new InvalidConfigurationException("[%s] %s", e.getKey(), ice.getMessage());
      }
    }
  }
}
