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

public class XMLServerConfiguration {

  private boolean acceptParams;

  private int backlogSize;

  private int readTimeout;

  private int writeTimeout;

  private List<XMLBind> bindList = new ArrayList<>();

  private List<XMLAllow> allowList = new ArrayList<>();

  public static XMLServerConfiguration parse(Element serverElement) {
    XMLServerConfiguration server = new XMLServerConfiguration();
    server.acceptParams = Boolean.parseBoolean(serverElement.getAttribute("accept-params"));
    server.backlogSize = Integer.parseInt(serverElement.getAttribute("backlog-size"));
    server.readTimeout = Integer.parseInt(serverElement.getAttribute("readTimeout"));
    server.writeTimeout = Integer.parseInt(serverElement.getAttribute("writeTimeout"));

    // Unmarshal dell'elemento "bind" (lista)
    NodeList bindNodes = serverElement.getElementsByTagName("bind");
    for (int i = 0; i < bindNodes.getLength(); i++) {
      Element bindElement = (Element) bindNodes.item(i);
      server.bindList.add(XMLBind.parse(bindElement));
    }

    // Unmarshal dell'elemento "allow"
    NodeList allowNodes = serverElement.getElementsByTagName("allow");
    for (int i = 0; i < allowNodes.getLength(); i++) {
      Element allowElement = (Element) allowNodes.item(i);
      server.allowList.add(XMLAllow.parse(allowElement));
    }

    return server;
  }

  public boolean getAcceptParams() {
    return acceptParams;
  }

  public Integer getBacklogSize() {
    return backlogSize;
  }

  public Integer getReadTimeout() {
    return readTimeout;
  }

  public Integer getWriteTimeout() {
    return writeTimeout;
  }

  public List<XMLBind> getBindList() {
    return Collections.unmodifiableList(bindList);
  }

  public List<XMLAllow> getAllowList() {
    return Collections.unmodifiableList(allowList);
  }
}
