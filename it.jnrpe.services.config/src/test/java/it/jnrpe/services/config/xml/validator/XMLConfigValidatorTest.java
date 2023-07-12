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
package it.jnrpe.services.config.xml.validator;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.services.config.InvalidConfigurationException;
import it.jnrpe.services.config.xml.ConfigValidator;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLConfigValidatorTest {

  private Document getDocument(String xml) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

    var db = dbf.newDocumentBuilder();
    return db.parse(new InputSource(new StringReader(xml)));
  }

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
    assertDoesNotThrow(
        () -> {
          new ConfigValidator().validate(getDocument(xmlText));
        });
  }

  @Test
  public void testInvalidSection() {
    String xmlText =
        """
                  <config>
                     <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                        <bind address="127.0.0.1:5666" SSL="false"/>
                        <allow ip="127.0.0.1"/>
                     </server>
                     <invalid>
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
                     </invalid>
                  </config>
                    """;

    var exc =
        assertThrows(
            InvalidConfigurationException.class,
            () -> new ConfigValidator().validate(getDocument(xmlText)));
    assertEquals("unknown section found: [/#document/config/invalid]", exc.getMessage());
  }

  @Test
  public void testMandatoryKeyMissing() {
    String xmlText =
        """
                   <config>
                     <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
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
    var exc =
        assertThrows(
            InvalidConfigurationException.class,
            () -> new ConfigValidator().validate(getDocument(xmlText)));
    assertEquals(
        "[/#document/config/server/bind] No enough occurrences found. Minimum allowed is 1 while the current count is 0",
        exc.getMessage());
  }

  @Test
  public void testInvalidSectionAttribute() {
    String xmlText =
        """
                   <config>
                     <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                        <allow ip="127.0.0.1"/>
                        <bind address="127.0.0.1:5666" SSL="false"/>
                     </server>
                     <commands>
                        <command name="check_disk_C" plugin_name="CHECK_DISK">
                           <arg name="path"  value="C:" />
                           <arg name="warning"  dalue="$ARG1$" />
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
    var exc =
        assertThrows(
            InvalidConfigurationException.class,
            () -> new ConfigValidator().validate(getDocument(xmlText)));
    assertEquals(
        "[/#document/config/commands/command/arg] Unknown attribute(s) found: [dalue]",
        exc.getMessage());
  }

  @Test
  public void testMissingMandatoryAttribute() {
    String xmlText =
        """
                   <config>
                     <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                        <allow ip="127.0.0.1" />
                        <bind SSL="false"/>
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
    var exc =
        assertThrows(
            InvalidConfigurationException.class,
            () -> new ConfigValidator().validate(getDocument(xmlText)));
    assertEquals(
        "[/#document/config/server/bind] Missing or empty mandatory key(s): [address]",
        exc.getMessage());
  }

  @Test
  public void testTooManySections() {
    String xmlText =
        """
                   <config>
                     <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                        <allow ip="127.0.0.1" />
                        <bind address="127.0.0.1:5666" SSL="false"/>
                     </server>
                     <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                        <allow ip="127.0.0.1" />
                        <bind address="127.0.0.3:5666" SSL="false"/>
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
    var exc =
        assertThrows(
            InvalidConfigurationException.class,
            () -> new ConfigValidator().validate(getDocument(xmlText)));
    assertEquals(
        "[/#document/config/server] Too many occurrences of [server] found. Maximum allowed is 1 while the current count is 2",
        exc.getMessage());
  }
}
