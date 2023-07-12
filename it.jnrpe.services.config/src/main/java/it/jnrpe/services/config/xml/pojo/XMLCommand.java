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

public class XMLCommand {
  private String name;

  private String pluginName;

  private List<XMLArg> argList = new ArrayList<>();

  public static XMLCommand parse(Element commandElement) {
    var command = new XMLCommand();
    command.name = commandElement.getAttribute("name");
    command.pluginName = commandElement.getAttribute("plugin_name");

    // Unmarshal degli elementi "arg" (lista)
    NodeList argNodes = commandElement.getElementsByTagName("arg");
    for (int j = 0; j < argNodes.getLength(); j++) {
      Element argElement = (Element) argNodes.item(j);
      command.argList.add(XMLArg.parse(argElement));
    }

    return command;
  }

  public String getName() {
    return name;
  }

  public String getPluginName() {
    return pluginName;
  }

  public List<XMLArg> getArgList() {
    return Collections.unmodifiableList(argList);
  }
}
