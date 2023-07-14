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

import java.net.URI;
import org.w3c.dom.Element;

public class XMLBind {
  private String address;

  private boolean ssl;

  public static XMLBind parse(Element bindElement) {
    XMLBind bind = new XMLBind();
    bind.address = bindElement.getAttribute("address");
    bind.ssl = Boolean.parseBoolean(bindElement.getAttribute("SSL"));
    return bind;
  }

  public String getAddress() {
    return address;
  }

  public boolean isSsl() {
    return ssl;
  }

  public int getPort() {
    try {
      return new URI("dummy://" + address).getPort();
    } catch (Exception ignored) {
    }
    return -1;
  }

  public String getHost() {
    try {
      return new URI("dummy://" + address).getHost();
    } catch (Exception ignored) {
    }
    return "";
  }
}
