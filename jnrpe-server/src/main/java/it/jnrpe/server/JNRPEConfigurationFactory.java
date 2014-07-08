/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
package it.jnrpe.server;

import java.io.File;

/**
 * Factory of configuration object.
 *
 * @author Massimiliano Ziccardi
 */
final class JNRPEConfigurationFactory {

    /**
     * Default constructor.
     */
    private JNRPEConfigurationFactory() {

    }

    /**
     * Creates a configuration object from the passed in configuration file.
     *
     * @param configurationFilePath
     *            Path to the configuration file
     * @return The configuration object
     * @throws ConfigurationException
     *             -
     */
    public static JNRPEConfiguration createConfiguration(final String configurationFilePath) throws ConfigurationException {
        JNRPEConfiguration conf = null;

        if (configurationFilePath.toLowerCase().endsWith(".conf") || configurationFilePath.toLowerCase().endsWith(".ini")) {
            conf = new IniJNRPEConfiguration();
        } else if (configurationFilePath.toLowerCase().endsWith(".xml")) {
            conf = new XmlJNRPEConfiguration();
        }

        if (conf == null) {
            throw new ConfigurationException("Config file name must end with either '.ini' " 
                                + "(ini file) or '.xml' (xml file). Received file name is : " + new File(configurationFilePath).getName());
        }

        conf.load(new File(configurationFilePath));

        return conf;
    }
}
