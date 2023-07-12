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

import org.w3c.dom.Element;

public class XMLArg {
  private String name;

  private String value;

  public static XMLArg parse(Element argElement) {
    var arg = new XMLArg();
    arg.name = argElement.getAttribute("name");
    arg.value = argElement.getAttribute("value");
    return arg;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
