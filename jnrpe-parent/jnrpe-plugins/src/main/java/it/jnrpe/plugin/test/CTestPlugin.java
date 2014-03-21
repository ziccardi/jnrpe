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
package it.jnrpe.plugin.test;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;

/**
 * A simple test plugin that returns the status as specified by the 'status'
 * parameter and as text the value of the 'text' parameter.
 *
 * @author Massimiliano Ziccardi
 *
 */
@Plugin(
        name = "TEST",
        description = "This is just a test plugin. Invoke it passing the String you want to get back and,\n" +
                        "optionally, the status you want.\n" +
                        "Return:\n" +
                        "If you don't specify the status parameters, the return code is 'OK'.\n" +
                        "The returned message is always the text you pass as parameter (-t/--text)\n\n" +
                        "Example Command Definition (inside the server configuration, in the command section)\n\n" +
                        "check_test : TEST --text $ARG1$ --status $ARG2$\n\n" +
                        "or, using the XML:\n\n" +
                        "<command name=\"test\" plugin_name=\"TEST\">\n" +
                        "   <arg name=\"text\" value=\"$ARG1$\"/>\n" +
                        "   <arg name=\"status\" value=\"$ARG2$\"/>\n" +
                        "</command>\n\n" + 
                        "Example invocation:\n\n" +
                        "./check_nrpe -n -H myjnrpeserver -c check_test -a 'Hello!critical'")
@PluginOptions({
    @Option(shortName="t",
                    longName="text",
                    description="the message to print",
                    required=true,
                    hasArgs=true,
                    argName="text",
                    optionalArgs=false,
                    option="text"
                    ),
   @Option(shortName="s",
                    longName="status",
                    description="the status to return (ok, warning, critical, unknown) - defaults to \"OK\"",
                    required=false,
                    hasArgs=true,
                    argName="statcode",
                    optionalArgs=false,
                    option="status"
                    )
              })
public class CTestPlugin implements IPluginInterface {

    /**
     * Executes the plugin. The accepter params are:
     * <UL>
     * <LI>--text : the text to be returned
     * <LI>--status : the status to be returned (ok, warning, critical. Any
     * other status is interpreted as UNKNOWN). The default value is 'ok'.
     * </UL>
     *
     * @param cl The command line
     * @return The return value
     */
    public final ReturnValue execute(final ICommandLine cl) {
        Status returnStatus;

        String statusParam = cl.getOptionValue("status", "ok");

        if (statusParam.equalsIgnoreCase("ok")) {
            returnStatus = Status.OK;
        } else if (statusParam.equalsIgnoreCase("critical")) {
            returnStatus = Status.CRITICAL;
        } else if (statusParam.equalsIgnoreCase("warning")) {
            returnStatus = Status.WARNING;
        } else {
            returnStatus = Status.UNKNOWN;
        }
        return new ReturnValue(returnStatus, "TEST : "
                + cl.getOptionValue("text"));
    }
}
