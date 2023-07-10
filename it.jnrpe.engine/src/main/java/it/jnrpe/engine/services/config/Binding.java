/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.engine.services.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Binding implements Cloneable {
  private String ip;
  private int port;
  private boolean ssl;
  private List<String> allow = new ArrayList<>();

  public Binding() {}

  public Binding(String ip, int port, boolean ssl) {
    this.ip = ip;
    this.port = port;
    this.ssl = ssl;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ipAddress) {
    this.ip = ipAddress;
  }

  public boolean isSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public List<String> getAllow() {
    return Collections.unmodifiableList(allow);
  }

  public void setAllow(List<String> allow) {
    this.allow = new ArrayList<>(allow);
  }

  public void addAllowedAddress(String ip) {
    this.allow.add(ip);
  }

  @Override
  public Binding clone() {
    try {
      return (Binding) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Never happens");
    }
  }

  @Override
  public String toString() {
    return "Binding{"
        + "ip='"
        + ip
        + '\''
        + ", port="
        + port
        + ", ssl="
        + ssl
        + ", allow="
        + allow
        + '}';
  }
}
