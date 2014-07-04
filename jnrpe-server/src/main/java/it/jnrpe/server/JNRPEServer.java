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

import it.jnrpe.JNRPE;
import it.jnrpe.JNRPEExecutionContext;
import it.jnrpe.JNRPEBuilder;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginConfigurationException;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginProxy;
import it.jnrpe.server.console.JNRPEConsole;
import it.jnrpe.server.plugins.DynaPluginRepository;

import java.io.File;
import java.net.UnknownHostException;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.util.HelpFormatter;

/**
 * The JNRPE Server entry point.
 * 
 * @author Massimiliano Ziccardi
 */
public final class JNRPEServer {

    /**
     * The default JNRPE server listening port.
     */
    private static final int DEFAULT_PORT = 5666;

    /**
     * The JNRPE Server version.
     */
    private static final String VERSION = JNRPEServer.class.getPackage().getImplementationVersion();

    /**
     * Default constructor.
     */
    private JNRPEServer() {
    }

    /**
     * Configure the command line parser.
     * 
     * @return The configuration
     */
    private static Group configureCommandLine() {
        DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        ArgumentBuilder aBuilder = new ArgumentBuilder();
        GroupBuilder gBuilder = new GroupBuilder();

        DefaultOption listOption = oBuilder.withLongName("list").withShortName("l").withDescription("Lists all the installed plugins").create();

        DefaultOption versionOption = oBuilder.withLongName("version").withShortName("v").withDescription("Print the server version number").create();

        DefaultOption helpOption = oBuilder.withLongName("help").withShortName("h").withDescription("Show this help").create();

        // DefaultOption pluginNameOption = oBuilder.withLongName("plugin")
        // .withShortName("p").withDescription("The plugin name")
        // .withArgument(
        // aBuilder.withName("name").withMinimum(1).withMaximum(1)
        // .create()).create();

        DefaultOption pluginHelpOption = oBuilder.withLongName("help").withShortName("h").withDescription("Shows help about a plugin")
                .withArgument(aBuilder.withName("name").withMinimum(1).withMaximum(1).create()).create();

        Group alternativeOptions = gBuilder.withOption(listOption).withOption(pluginHelpOption).create();

        DefaultOption confOption = oBuilder.withLongName("conf").withShortName("c").withDescription("Specifies the JNRPE configuration file")
                .withArgument(aBuilder.withName("path").withMinimum(1).withMaximum(1).create()).withChildren(alternativeOptions).withRequired(true)
                .create();

        DefaultOption interactiveOption = oBuilder.withLongName("interactive").withShortName("i")
                .withDescription("Starts JNRPE in command line mode").create();

        Group jnrpeOptions = gBuilder.withOption(confOption).withOption(interactiveOption).withMinimum(1).create();

        return gBuilder.withOption(versionOption).withOption(helpOption).withOption(jnrpeOptions).create();
    }

    /**
     * Parses the command line.
     * 
     * @param vsArgs
     *            The command line
     * @return The parsed command line
     */
    private static CommandLine parseCommandLine(final String[] vsArgs) {
        try {
            Group opts = configureCommandLine();
            // configure a HelpFormatter
            HelpFormatter hf = new HelpFormatter();

            // configure a parser
            Parser p = new Parser();
            p.setGroup(opts);
            p.setHelpFormatter(hf);
            // p.setHelpTrigger("--help");
            return p.parse(vsArgs);
        } catch (OptionException oe) {
            printUsage(oe);
        } catch (Exception e) {
            e.printStackTrace();
            // Should never happen...
        }
        return null;
    }

    /**
     * Prints the help about a plugin.
     * 
     * @param pr
     *            The plugin repository
     * @param pluginName
     *            The plugin name
     */
    private static void printHelp(final IPluginRepository pr, final String pluginName) {
        try {
            PluginProxy pp = (PluginProxy) pr.getPlugin(pluginName);

            // CPluginProxy pp =
            // CPluginFactory.getInstance().getPlugin(sPluginName);
            if (pp == null) {
                System.out.println("Plugin " + pluginName + " does not exists.");
            } else {
                pp.printHelp();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Prints the JNRPE Server version.
     */
    private static void printVersion() {
        System.out.println("JNRPE version " + VERSION);
        System.out.println("Copyright (c) 2011 Massimiliano Ziccardi");
        System.out.println("Licensed under the Apache License, Version 2.0");
        System.out.println();
    }

    /**
     * Prints the JNRPE Server usage and, eventually, the error about the last
     * invocation.
     * 
     * @param e
     *            The last error. Can be null.
     */
    @SuppressWarnings("unchecked")
    private static void printUsage(final Exception e) {
        printVersion();
        if (e != null) {
            System.out.println(e.getMessage() + "\n");
        }

        HelpFormatter hf = new HelpFormatter();

        StringBuilder sbDivider = new StringBuilder("=");
        while (sbDivider.length() < hf.getPageWidth()) {
            sbDivider.append("=");
        }

        // DISPLAY SETTING
        hf.getDisplaySettings().clear();
        hf.getDisplaySettings().add(DisplaySetting.DISPLAY_GROUP_EXPANDED);
        hf.getDisplaySettings().add(DisplaySetting.DISPLAY_PARENT_CHILDREN);

        // USAGE SETTING

        hf.getFullUsageSettings().clear();
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_PARENT_CHILDREN);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_EXPANDED);

        hf.setDivider(sbDivider.toString());

        hf.setGroup(configureCommandLine());
        hf.print();
        System.exit(0);
    }

    /**
     * Loads the JNRPE configuration from the INI or the XML file.
     * 
     * @param configurationFilePath
     *            The path to the configuration file
     * @return The parsed configuration.
     * @throws ConfigurationException
     *             -
     */
    private static JNRPEConfiguration loadConfiguration(final String configurationFilePath) throws ConfigurationException {
        File confFile = new File(configurationFilePath);

        if (!confFile.exists() || !confFile.canRead()) {
            throw new ConfigurationException("Cannot access config file : " + configurationFilePath);
        }

        return JNRPEConfigurationFactory.createConfiguration(configurationFilePath);

    }

    /**
     * Loads a plugin repository from a directory.
     * 
     * @param sPluginDirPath
     *            The path to the directory
     * @return The plugin repository
     * @throws PluginConfigurationException
     *             -
     */
    private static IPluginRepository loadPluginDefinitions(final String sPluginDirPath) throws PluginConfigurationException {
        File fDir = new File(sPluginDirPath);
        DynaPluginRepository repo = new DynaPluginRepository();
        repo.load(fDir);

        return repo;
    }

    /**
     * Prints the list of installed plugins.
     * 
     * @param pr
     *            The plugin repository
     */
    private static void printPluginList(final IPluginRepository pr) {
        System.out.println("List of installed plugins : ");

        for (PluginDefinition pd : pr.getAllPlugins()) {
            System.out.println("  * " + pd.getName());
        }

        System.exit(0);
    }

    /**
     * The main method.
     * 
     * @param args
     *            The command line
     */
    public static void main(final String[] args) {
        CommandLine cl = parseCommandLine(args);
        if (cl.hasOption("--help")) {
            if (!cl.hasOption("--conf")) {
                printUsage(null);
            }
        }

        if (cl.hasOption("--version")) {
            printVersion();
            System.exit(0);
        }

        JNRPEConfiguration conf = null;
        try {
            conf = loadConfiguration((String) cl.getValue("--conf"));
        } catch (Exception e) {
            System.out.println("Unable to parse the configuration at " + cl.getValue("--conf") + ". The error is : " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }

        String sPluginPath = conf.getServerSection().getPluginPath();
        if (sPluginPath == null) {
            System.out.println("Plugin path has not been specified");
            System.exit(-1);
        }
        File fPluginPath = new File(sPluginPath);

        if (fPluginPath.exists()) {
            if (!fPluginPath.isDirectory()) {
                System.out.println("Specified plugin path ('" + sPluginPath + "') must be a directory");
                System.exit(-1);
            }
        } else {
            System.out.println("Specified plugin path ('" + sPluginPath + "') do not exist");
            System.exit(-1);
        }

        IPluginRepository pr = null;
        try {
            pr = loadPluginDefinitions(conf.getServerSection().getPluginPath());
        } catch (PluginConfigurationException e) {
            System.out.println("An error has occurred while parsing " + "the plugin packages : " + e.getMessage());
            System.exit(-1);
        }

        if (cl.hasOption("--help") && cl.getValue("--help") != null) {
            printHelp(pr, (String) cl.getValue("--help"));
        }

        if (cl.hasOption("--list")) {
            printPluginList(pr);
        }

        CommandRepository cr = conf.createCommandRepository();

        JNRPEBuilder builder = JNRPEBuilder.forRepositories(pr, cr).acceptParams(conf.getServerSection().acceptParams())
                .withMaxAcceptedConnections(conf.getServerSection().getBacklogSize()).withReadTimeout(conf.getServerSection().getReadTimeout())
                .withWriteTimeout(conf.getServerSection().getWriteTimeout()).withListener(new EventLoggerListener());

        for (String sAcceptedAddress : conf.getServerSection().getAllowedAddresses()) {
            builder.acceptHost(sAcceptedAddress);
        }

        JNRPE jnrpe = builder.build();

        for (BindAddress bindAddress : conf.getServerSection().getBindAddresses()) {
            int iPort = DEFAULT_PORT;
            String[] vsParts = bindAddress.getBindingAddress().split(":");
            String sIp = vsParts[0];
            if (vsParts.length > 1) {
                iPort = Integer.parseInt(vsParts[1]);
            }

            try {
                jnrpe.listen(sIp, iPort, bindAddress.isSSL());
            } catch (UnknownHostException e) {
                System.out.println(String.format("Error binding the server to %s:%d : %s", sIp, iPort, e.getMessage()));
            }
        }

        if (cl.hasOption("--interactive")) {
            new JNRPEConsole(jnrpe, pr, cr).start();
            System.exit(0);
        }
    }
}
