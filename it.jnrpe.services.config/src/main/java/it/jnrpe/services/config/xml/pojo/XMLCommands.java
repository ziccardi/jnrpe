/*******************************************************************************
 * Copyright (C) 2023, Massimiliano Ziccardi
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
package it.jnrpe.services.config.xml.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLCommands {
  private List<XMLCommand> commandList = new ArrayList<>();

  public static XMLCommands parse(Element commandsElement) {
    XMLCommands commands = new XMLCommands();

    // Unmarshal degli elementi "command" (lista)
    NodeList commandNodes = commandsElement.getElementsByTagName("command");
    for (int i = 0; i < commandNodes.getLength(); i++) {
      Element commandElement = (Element) commandNodes.item(i);

      commands.commandList.add(XMLCommand.parse(commandElement));
    }

    return commands;
  }

  public List<XMLCommand> getCommandList() {
    return Collections.unmodifiableList(commandList);
  }
}
