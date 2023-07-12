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
package it.jnrpe.services.config.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;

public class XmlJnrpeConfigProviderTest {
  @Test
  public void testValidConfig() throws Exception {
    String xmlText =
        """
                        <config>
                             <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                                <bind address="127.0.0.1:5666" SSL="false"/>
                                <allow ip="127.0.0.1"/>
                             </server>
                             <commands>
                                <command name="check_disk_C" plugin_name="CHECK_DISK">
                                   <arg name="path"  value="C:" />
                                   <arg name="warning"  value="$ARG1$" />
                                   <arg name="critical"  value="$ARG2$" />
                                </command>
                                <command name="check_disk_E" plugin_name="CHECK_DISK">
                                   <arg name="path"  value="E:" />
                                   <arg name="warning"  value="$ARG1$" />
                                   <arg name="critical"  value="$ARG2$" />
                                </command>
                             </commands>
                          </config>
                                """;
    try (var bin = new ByteArrayInputStream(xmlText.getBytes())) {
      var conf = new XmlJnrpeConfigProvider().parseConf(bin);
      var jnrpeConf = new XMLJNRPEConfigurationProxy(conf);
      assertNotNull(jnrpeConf.getServer());
      assertNotNull(jnrpeConf.getCommands());
    }
  }
}
