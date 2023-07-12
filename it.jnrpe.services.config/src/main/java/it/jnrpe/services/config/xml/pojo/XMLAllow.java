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

public class XMLAllow {
  private String ip;

  public static XMLAllow parse(Element allowElement) {
    var allow = new XMLAllow();
    allow.ip = allowElement.getAttribute("ip");
    return allow;
  }

  public String getIp() {
    return ip;
  }
}
