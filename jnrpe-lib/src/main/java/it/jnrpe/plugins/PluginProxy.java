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
package it.jnrpe.plugins;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.internal.InjectionUtils;

import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.lang.StringUtils;

/**
 * This class was intended to abstract the kind of plugin to execute. Hides
 * command line parsing from command invoker.
 * 
 * @author Massimiliano Ziccardi
 * 
 * @version $Revision: 1.0 $
 */
public final class PluginProxy extends PluginBase {
    /**
     * The plugin instance proxied by this object.
     */
    private final IPluginInterface proxiedPlugin;

    /**
     * The plugin definition of the plugin proxied by this object.
     */
    private final PluginDefinition proxyedPluginDefinition;

    /**
     * The command line definition as requested by the Apache commons cli.
     * library.
     */
    private Group mainOptionsGroup = null;

    /**
     * The proxied plugin description.
     */
    private final String description;

    /**
     * Instantiate a new plugin proxy.
     * 
     * @param plugin
     *            The plugin to be proxied
     * @param pluginDef
     *            The plugin definition of the plugin to be proxied
     */
    public PluginProxy(final IPluginInterface plugin, final PluginDefinition pluginDef) {
        proxiedPlugin = plugin;
        proxyedPluginDefinition = pluginDef;
        description = proxyedPluginDefinition.getDescription();

        GroupBuilder gBuilder = new GroupBuilder();

        for (PluginOption po : pluginDef.getOptions()) {
            gBuilder = gBuilder.withOption(po.toOption());
        }

        mainOptionsGroup = gBuilder.create();
    }

    /**
     * Returns a collection of all the options accepted by this plugin.
     * 
    
     * @return a collection of plugin options. */
    public Collection<PluginOption> getOptions() {
        return proxyedPluginDefinition.getOptions();
    }

    /**
     * Executes the proxied plugin passing the received arguments as parameters.
     * 
     * @param argsAry The parameters to be passed to the plugin
     * @return The return value of the plugin.
     * @throws BadThresholdException if an error occurs parsing the threshold
     */
    public ReturnValue execute(final String[] argsAry) throws BadThresholdException {
        // CommandLineParser clp = new PosixParser();
        try {
            HelpFormatter hf = new HelpFormatter();
            // configure a parser
            Parser cliParser = new Parser();
            cliParser.setGroup(mainOptionsGroup);
            cliParser.setHelpFormatter(hf);
            CommandLine cl = cliParser.parse(argsAry);
            
            // Inject the context...
            InjectionUtils.inject(proxiedPlugin, getContext());
            
            Thread.currentThread().setContextClassLoader(proxiedPlugin.getClass().getClassLoader());

            ReturnValue retValue = proxiedPlugin.execute(new PluginCommandLine(cl));

            if (retValue == null) {
                String msg = "Plugin [" + getPluginName() + "] with args [" + StringUtils.join(argsAry) + "] returned null";

                retValue = new ReturnValue(Status.UNKNOWN, msg);
            }

            return retValue;
        } catch (OptionException e) {
            
            String msg = e.getMessage();
            LOG.error(getContext(), "Error parsing plugin '" + getPluginName() + "' command line : " + msg, e);
            return new ReturnValue(Status.UNKNOWN, msg);
        }
    }

    /**
     * Prints the help related to the plugin to a specified output.
     * 
     * @param out
     *            the writer where the help should be written
     */
    public void printHelp(final PrintWriter out) {
        HelpFormatter hf = new HelpFormatter();
        StringBuilder sbDivider = new StringBuilder("=");
        while (sbDivider.length() < hf.getPageWidth()) {
            sbDivider.append('=');
        }
        out.println(sbDivider.toString());
        out.println("PLUGIN NAME : " + proxyedPluginDefinition.getName());
        if (description != null && description.trim().length() != 0) {
            out.println(sbDivider.toString());
            out.println("Description : ");
            out.println();
            out.println(description);
        }

        hf.setGroup(mainOptionsGroup);
        // hf.setHeader(m_pluginDef.getName());
        hf.setDivider(sbDivider.toString());
        hf.setPrintWriter(out);
        hf.print();
        // hf.printHelp(m_pluginDef.getName(), m_Options);
    }

    /**
     * Prints the help related to the plugin to standard output.
     */
    // FIXME : the output should be a parameter
    public void printHelp() {
        printHelp(new PrintWriter(System.out));
    }

    /**
     * Not used.
     * 
     * @param cl
     *            Not used
    
     * @return null. * @see it.jnrpe.plugins.IPluginInterface#execute(ICommandLine)
     */
    @Override
    public ReturnValue execute(final ICommandLine cl) {
        return null;
    }

    /**
     * Method getPluginName.
     * @return String
     */
    @Override
    protected String getPluginName() {
        return proxyedPluginDefinition.getName();
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "PluginProxy [proxiedPlugin=" + proxiedPlugin + ", proxyedPluginDefinition=" + proxyedPluginDefinition + ", mainOptionsGroup="
                + mainOptionsGroup + ", description=" + description + "]";
    }
}
