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

import it.jnrpe.JNRPE;
import it.jnrpe.JNRPEBuilder;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.OSGILogFactory;

/**
 * The JNRPE bundle activator. Automatically receive the JNRPE configuration and
 * starts listening on the configured ports/addresses.
 * 
 * @author Massimiliano Ziccardi
 */
public class JNRPEBundleActivator implements BundleActivator, ManagedService {

    /**
     * The JNRPE OSGI bundle PID.
     */
    private static final String PID = "it.jnrpe.osgi";

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JNRPEBundleActivator.class);

    /**
     * The OSGI bundle tracker. The {@link JNRPEBundleTracker} object is used to
     * detect the installation or the update of a plugin package.
     */
    private BundleTracker bundleTracker;

    /**
     * The JNRPE plugins repository.
     */
    private IPluginRepository pluginRepository;
    
    /**
     * The JNRPE commands repository.
     */
    private CommandRepository commandRepository;

    /**
     * The JNRPE engine.
     */
    private JNRPE jnrpeEngine;

    /**
     * Automatically called by the OSGI layer when a new configuration is ready.
     * 
     * @param properties The new configuration
     * @throws ConfigurationException on problems loading the configuration
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final void updated(final Dictionary properties) throws ConfigurationException {
        if (properties == null) {
            LOG.info("Empty configuration received. JNRPE Server not started");
            return;
        }

        LOG.info("New configuration received");
        if (jnrpeEngine != null) {
            LOG.info("Shutting down JNRPE listener...");
            jnrpeEngine.shutdown();
        }

        String bindAddress = (String) properties.get("bind_address");
        String allowAddress = (String) properties.get("allow_address");

        // Parsing command definition...
        for (CommandDefinition cd : parseCommandDefinitions(properties)) {
            LOG.info("Adding command definition for command '{}'", cd.getName());
            commandRepository.addCommandDefinition(cd);
        }

        // TODO: EventLoggerListener can be a single instance.
        JNRPEBuilder builder = JNRPEBuilder.forRepositories(pluginRepository, commandRepository).withListener(new EventLoggerListener());

        if (allowAddress == null || allowAddress.trim().length() == 0) {
            allowAddress = "127.0.0.1";
        }

        for (String addr : allowAddress.split(",")) {

            LOG.info("Allowing requests from : {}", addr);

            if (addr.trim().length() == 0) {
                continue;
            }

            builder.acceptHost(addr);
            // engine.addAcceptedHost(addr);
        }

        JNRPE engine = builder.build();

        if (bindAddress != null && bindAddress.trim().length() != 0) {
            JNRPEBindingAddress address = new JNRPEBindingAddress(bindAddress);
            try {

                LOG.info("Listening on : {}:{} - SSL:{}", address.getAddress(), address.getPort(), address.isSsl());

                engine.listen(address.getAddress(), address.getPort(), address.isSsl());
            } catch (UnknownHostException e) {
                LOG.error("Bad binding address: {}", bindAddress);
                throw new ConfigurationException("bind_address", e.getMessage(), e);
            }
        } else {
            throw new ConfigurationException("bind_address", "Binding address can't be empty");
        }
        jnrpeEngine = engine;
    }

    /**
     * Parses the command definitions from the recived configuration.
     * 
     * @param props the configuration
     * @return a collection of the command read from the configuration
     */
    private Collection<CommandDefinition> parseCommandDefinitions(final Dictionary<String, String> props) {

        List<CommandDefinition> res = new ArrayList<CommandDefinition>();

        final String prefix = "command.";
        final int prefixLen = prefix.length();

        Enumeration<String> en = props.keys();
        while (en.hasMoreElements()) {
            String key = en.nextElement();
            if (key.startsWith(prefix)) {
                String commandName = key.substring(prefixLen);

                String commandLine = props.get(key);
                String[] elements = StringUtils.split(commandLine, false);
                String pluginName = elements[0];

                StringBuilder cmdLine = new StringBuilder();

                for (int i = 1; i < elements.length; i++) {
                    cmdLine.append(quoteAndEscape(elements[i])).append(' ');
                }

                CommandDefinition cd = new CommandDefinition(commandName, pluginName);
                cd.setArgs(cmdLine.toString());
                res.add(cd);
            }
        }

        return res;
    }

    /**
     * Quotes a string.
     * 
     * @param string
     *            The string to be quoted
     * @return The quoted string
     */
    private String quoteAndEscape(final String string) {
        if (string.indexOf(' ') == -1) {
            return string;
        }

        return "\"" + string.replaceAll("\"", "\\\"") + "\"";
    }

    /**
     * Initializes the bundle.
     * 
     * @param context the bundle context
     * @throws Exception on any error
     */
    public final void start(final BundleContext context) throws Exception {

        OSGILogFactory.initOSGI(context);

        pluginRepository = new PluginRepository();
        commandRepository = new CommandRepository();

        bundleTracker = new JNRPEBundleTracker(context, pluginRepository, commandRepository);
        bundleTracker.open();
        // Register the managed service...
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.SERVICE_PID, PID);

        ServiceRegistration servReg = context.registerService(ManagedService.class.getName(), this, props);
        servReg.setProperties(props);
        LOG.info("JNRPE bundle started");
    }

    /**
     * Stops the JNRPE bundle.
     * 
     * @param context the bundle context
     * @throws Exception on any error
     */
    public final void stop(final BundleContext context) throws Exception {
        if (jnrpeEngine != null) {
            jnrpeEngine.shutdown();
        }

    }

}
