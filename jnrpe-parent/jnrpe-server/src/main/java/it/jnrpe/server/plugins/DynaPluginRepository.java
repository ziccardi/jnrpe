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
package it.jnrpe.server.plugins;

import it.jnrpe.plugins.PluginConfigurationException;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.PluginRepositoryUtil;
import it.jnrpe.utils.StreamManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object represent a Plugin repository loaded dynamically from file
 * system.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class DynaPluginRepository extends PluginRepository {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(DynaPluginRepository.class);

    /**
     * Filter for jar files.
     */
    private static final JarFilefilter JAR_FILE_FILTER = new JarFilefilter();

    /**
     * File filter class for jar files.
     *
     * @author Massimiliano Ziccardi
     */
    private static class JarFilefilter implements FilenameFilter {
        /**
         * Returns <code>true</code> if the file name ends with ".jar".
         *
         * @param dir
         *            The directory containing the file
         * @param name
         *            The filename
         * @return <code>true</code> if the file name ends with ".jar".
         */
        public boolean accept(final File dir, final String name) {
            return name.endsWith(".jar");
        }
    }

    /**
     * Loads all the plugins definitions from the given directory.
     *
     * @param fDir
     *            The directory where the plugins are installed.
     * @throws PluginConfigurationException
     *             -
     */
    private void configurePlugins(final File fDir)
            throws PluginConfigurationException {
        LOG.trace("READING PLUGIN CONFIGURATION FROM DIRECTORY "
                + fDir.getName());
        StreamManager streamMgr = new StreamManager();
        // File[] vfJars = fDir.listFiles(new FileFilter()
        // {
        //
        // public boolean accept(File f)
        // {
        // return f.getName().endsWith(".jar");
        // }
        //
        // });

        File[] vfJars = fDir.listFiles(JAR_FILE_FILTER);

        if (vfJars == null || vfJars.length == 0) {
            return;
        }

        // Initializing classloader
        List<URL> vUrls = new ArrayList<URL>(vfJars.length);

        ClassLoader ul = null;

        for (int j = 0; j < vfJars.length; j++) {
            try {
                // urls[j] = vfJars[j].toURI().toURL();
                vUrls.add(vfJars[j].toURI().toURL());
            } catch (MalformedURLException e) {
                // should never happen
            }
        }

        ul = new JNRPEClassLoader(vUrls);

        try {
            InputStream in =
                    streamMgr.handle(ul.getResourceAsStream("plugin.xml"));
            if (in == null) {
                // Error : No plugin.xml
                // TODO : must throw an exception
                return;
            }

            PluginRepositoryUtil.loadFromXmlPluginPackageDefinitions(this, ul,
                    in);
        } finally {
            streamMgr.closeAll();
        }
    }

    /**
     * Loops through all the directories present inside the JNRPE plugin
     * directory.
     *
     * @param fDirectory
     *            The JNRPE plugins directory
     * @throws PluginConfigurationException
     *             -
     */
    public final void load(final File fDirectory)
            throws PluginConfigurationException {
        File[] vFiles = fDirectory.listFiles();
        if (vFiles != null) {
            for (File f : vFiles) {
                if (f.isDirectory()) {
                    configurePlugins(f);
                }
            }
        }
    }
}
