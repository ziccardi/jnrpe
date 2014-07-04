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
package it.jnrpe.server.console;

import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPE;
import it.jnrpe.ReturnValue;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginOption;
import it.jnrpe.plugins.PluginProxy;
import it.jnrpe.plugins.UnknownPluginException;
import it.jnrpe.utils.internal.InjectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import jline.console.ConsoleReader;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;

public class PluginCommand extends ConsoleCommand {

    public final static String NAME = "plugin:";

    private final String pluginName;
    private final IPluginRepository pluginRepository;
    private final IPluginInterface plugin;

    private final IJNRPEExecutionContext context;
    
    private final Charset charset;
    
    public PluginCommand(ConsoleReader consoleReader, IPluginRepository pluginRepo, JNRPE jnrpe, String pluginName) throws UnknownPluginException {
        super(consoleReader, jnrpe);
        this.pluginName = pluginName;
        this.pluginRepository = pluginRepo;
        this.plugin = pluginRepository.getPlugin(pluginName);
        this.charset = jnrpe.getExecutionContext().getCharset();
        this.context = jnrpe.getExecutionContext();
    }

    public boolean execute(final String[] args) throws Exception {

        Parser p = new Parser();
        p.setGroup(getCommandLineGroup());
        try {
            p.parse(args);
        } catch (Exception e) {
            getConsole().println();
            // getConsole().println("\u001B[1mERROR:\u001B[0m " +
            // e.getMessage());
            getConsole().println(highlight("ERROR: ") + e.getMessage());
            getConsole().println();

            printHelp();
            return false;
        }
        PluginProxy plugin = (PluginProxy) pluginRepository.getPlugin(pluginName);
        InjectionUtils.inject(plugin, context);
        //plugin.setContext(context);
        ReturnValue retVal = plugin.execute(args);

        getConsole().println(retVal.getMessage());
        return false;
    }

    public String getName() {
        return NAME + pluginName;
    }

    private Option toOption(PluginOption po) {
        DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();

        oBuilder.withShortName(po.getOption()).withDescription(po.getDescription()).withRequired("true".equalsIgnoreCase(po.getRequired()));

        if (po.getLongOpt() != null) {
            oBuilder.withLongName(po.getLongOpt());
        }

        if (po.hasArgs()) {
            ArgumentBuilder aBuilder = new ArgumentBuilder();

            if (po.getArgName() != null) {
                aBuilder.withName(po.getArgName());
            }

            if (po.getArgsOptional()) {
                aBuilder.withMinimum(0);
            }

            if (po.getArgsCount() != null) {
                aBuilder.withMaximum(po.getArgsCount());
            } else {
                aBuilder.withMaximum(1);
            }

            if (po.getValueSeparator() != null && po.getValueSeparator().length() != 0) {
                aBuilder.withInitialSeparator(po.getValueSeparator().charAt(0));
                aBuilder.withSubsequentSeparator(po.getValueSeparator().charAt(0));
            }
            oBuilder.withArgument(aBuilder.create());
        }

        return oBuilder.create();
    }

    private Group getCommandLineGroup() {
        PluginProxy pp = (PluginProxy) plugin;
        GroupBuilder gBuilder = new GroupBuilder();

        for (PluginOption po : pp.getOptions()) {
            gBuilder = gBuilder.withOption(toOption(po));
        }

        return gBuilder.create();
    }

    public String getCommandLine() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Group g = getCommandLineGroup();
        HelpFormatter hf = new HelpFormatter(null, null, null, getConsole().getTerminal().getWidth());
        hf.setGroup(g);
        hf.setPrintWriter(new PrintWriter(new OutputStreamWriter(bout, charset)));
        hf.printUsage();

        String usage = new String(bout.toByteArray(), charset);

        String[] lines = usage.split("\\n");

        StringBuilder res = new StringBuilder();

        for (int i = 1; i < lines.length; i++) {
            res.append(lines[i]);
        }

        return res.toString();
    }

    public void printHelp() throws IOException {
        Group g = getCommandLineGroup();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        HelpFormatter hf = new HelpFormatter("  ", null, null, getConsole().getTerminal().getWidth());
        hf.setGroup(g);

        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout, charset));
        hf.setPrintWriter(pw);
        hf.printHelp();

        pw.flush();

        // getConsole().println("\u001B[1mCommand Line:\u001B[0m ");
        getConsole().println(highlight("Command Line: "));
        getConsole().println("  " + getName() + " " + getCommandLine());
        getConsole().println(highlight("Usage:"));
        getConsole().println(new String(bout.toByteArray(), charset));

    }

}
