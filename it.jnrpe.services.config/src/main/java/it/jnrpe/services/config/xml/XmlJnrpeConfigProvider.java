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
package it.jnrpe.services.config.xml;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.engine.services.config.IJNRPEConfig;
import it.jnrpe.services.config.InvalidConfigurationException;
import it.jnrpe.services.config.xml.pojo.XMLConfiguration;
import java.io.InputStream;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class XmlJnrpeConfigProvider implements IConfigProvider {
  @Override
  public String getProviderName() {
    return "XML";
  }

  private Optional<IJNRPEConfig> parseConfig() throws InvalidConfigurationException {
    ServiceLoader<IConfigSource> configSourceServiceLoader =
        ServiceLoader.load(IConfigSource.class);
    var configServicesCount = configSourceServiceLoader.stream().count();

    if (configServicesCount > 1) {
      EventManager.warn(
          "More than one config service has been found [%d]. Only first one will be used",
          configServicesCount);
      return Optional.empty();
    }

    Optional<IConfigSource> optionalConfigSource = configSourceServiceLoader.findFirst();

    if (optionalConfigSource.isPresent()
        && optionalConfigSource.get().getConfigType().equalsIgnoreCase("xml")) {
      try {
        var xmlConf = parseConf(optionalConfigSource.get().getConfigStream());

        // config is ok. Generate the JNRPEConfig
        // return Optional.of(xmlConf.toJNRPEConfig());
        return Optional.of(new XMLJNRPEConfigurationProxy(xmlConf));
      } catch (Exception e) {
        throw new InvalidConfigurationException("Malformed XML configuration file");
      }
    }

    EventManager.warn("No config services have been provided");
    return Optional.empty();
  }

  XMLConfiguration parseConf(InputStream configStream) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

    var db = dbf.newDocumentBuilder();

    Document document = db.parse(configStream);
    return XMLConfiguration.parse(document);
  }

  @Override
  public Optional<IJNRPEConfig> getConfig() {
    try {
      return parseConfig();
    } catch (Exception e) {
      EventManager.error("Unable to parse XML configuration: %s", e.getMessage());
    }
    return Optional.empty();
  }

  @Override
  public String generateSampleConfig() {
    return """
            <config>
                <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
                    <!-- Set up the port on which JNRPE will listen. -->
                    <bind address="127.0.0.1:5666" SSL="false"/>
                    <!-- The `allow` tag specifies the IP addresses that are permitted to establish a connection with JNRPE.
                      -- By repeating this tag, multiple IPs can be allowed.
                    <allow ip="127.0.0.1"/>
                    -->
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
  }
}
