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
package it.jnrpe.osgi;

import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;
import it.jnrpe.utils.StreamManager;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks the installation of new bundles inside the OSGI container to detect if
 * a new JNRPE plugin package has been installed.
 * 
 * @author Massimiliano Ziccardi
 */
public class JNRPEBundleTracker extends BundleTracker {

    /**
     * The name of the attribute that will be searched inside the new bundle
     * manifest.
     */
    private static final String JNRPE_PLUGIN_PACKAGE_NAME = "JNRPE-PluginPackage-Name";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JNRPEBundleTracker.class);

    /**
     * The JNRPE plugins repository.
     */
    private final IPluginRepository pluginRepository;
    
    /**
     * The JNRPE commands repository.
     */
    private final CommandRepository commandRepository;

    /**
     * Builds the bundle tracker.
     * 
     * @param context the bundle context
     * @param pluginRepo the plugins repository
     * @param commandRepo the commands repository
     */
    public JNRPEBundleTracker(final BundleContext context, final IPluginRepository pluginRepo, final CommandRepository commandRepo) {
        super(context, Bundle.ACTIVE | Bundle.STOPPING, null);
        pluginRepository = pluginRepo;
        commandRepository = commandRepo;
    }

    /**
     * Loads the plugins definition from the bundle.
     * First searches for the jnrpe_plugins.xml file. 
     * If that file is not found, tries to read the 'plugin.xml' file.
     * @param cl the classloader to be used to load the definition file
     * @return The stream to the definition file
     */
    private InputStream getPluginConfigStream(final ClassLoader cl) {
        InputStream in = null;

        in = cl.getResourceAsStream("jnrpe_plugins.xml");

        if (in == null) {
            // fallback to the old plugin.xml file...
            in = cl.getResourceAsStream("plugin.xml");
        }

        return in;
    }

    @Override
    public final Object addingBundle(final Bundle bundle, final BundleEvent event) {

        String pluginPackageClassName = (String) bundle.getHeaders().get(JNRPE_PLUGIN_PACKAGE_NAME);

        if (pluginPackageClassName != null) {
            LOG.info("Plugin package found: {} ", pluginPackageClassName);

            StreamManager sm = new StreamManager();
            // The bundle is a plugin package...
            try {
                BundleDelegatingClassLoader bdc = AccessController.doPrivileged(new PrivilegedAction<BundleDelegatingClassLoader>() {
                    public BundleDelegatingClassLoader run() {
                        return new BundleDelegatingClassLoader(bundle);
                    }
                });

                // the 'plugin.xml' file is deprecated. Search for the new
                // jnrpe_plugins.xml file and then fallback to the old
                // plugin.xml

                InputStream in = sm.handle(getPluginConfigStream(bdc));

                // FIXME: check that 'in' is not null

                Collection<PluginDefinition> pdList = PluginRepositoryUtil.loadFromXmlPluginPackageDefinitions(bdc, in);

                for (PluginDefinition pd : pdList) {
                    LOG.info("Adding plugin '{}' to the repository", pd.getName());
                    pluginRepository.addPluginDefinition(pd);
                }

            } catch (Exception e) {
                LOG.error("Error adding plugin to the repository", e);
            } finally {
                sm.closeAll();
            }
        }

        return bundle;
    }

    @Override
    public final void remove(final Bundle bundle) {
        String pluginPackageClassName = (String) bundle.getHeaders().get(JNRPE_PLUGIN_PACKAGE_NAME);

        if (pluginPackageClassName != null) {

            StreamManager sm = new StreamManager();

            // The bundle is a plugin package...
            try {
                BundleDelegatingClassLoader bdc = AccessController.doPrivileged(new PrivilegedAction<BundleDelegatingClassLoader>() {
                    public BundleDelegatingClassLoader run() {
                        return new BundleDelegatingClassLoader(bundle);
                    }
                });

                InputStream in = sm.handle(getPluginConfigStream(bdc));

                Collection<PluginDefinition> pdList = PluginRepositoryUtil.loadFromXmlPluginPackageDefinitions(bdc, in);

                for (PluginDefinition pd : pdList) {
                    // First remove all the commands using the plugin...
                    for (CommandDefinition cd : commandRepository.getAllCommandDefinition(pd.getName())) {
                        LOG.info("Removing command '{}' from the repository", cd.getName());
                        commandRepository.removeCommandDefinition(cd);
                    }

                    LOG.info("Removing plugin '{}' from the repository", pd.getName());
                    pluginRepository.removePluginDefinition(pd);
                }
            } catch (Exception e) {
                LOG.error("Error removing plugin from the repository", e);
            } finally {
                sm.closeAll();
            }
        }
    }
}
