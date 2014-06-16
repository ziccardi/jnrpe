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
package it.jnrpe.plugin;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This plugin checks the number of users currently logged in on the local
 * system and generates a critical or an error status according to the passed-in
 * thresholds.
 *
 * @author Frederico Campos
 *
 */
@Plugin(
		name = "CHECK_USERS",
		description = "This plugin checks the number of users currently logged in on the\n" +
				"local system and generates an error if the number exceeds the thresholds specified.\n" +
				"EXAMPLES\n" +
				"The example will be based upon the following command definition (ini file)\n\n" +

			    "check_user : CHECK_USER --warning $ARG1$ --critical $ARG2$\n\n"+
			
			    "* Example 1 (Windows and Unix)\n"+
			    "The following example will give a WARNING if the number of logged in users exceeds 10 and a CRITICAL message if the number of users exceeds 20\n\n"+
			
				"check_nrpe -H myjnrpeserver -c check_users -a '10:!20:'")

@PluginOptions({

	@Option(shortName="w",
			longName="warning",
			description="Set WARNING status if more than INTEGER users are logged in",
			required=false,
			hasArgs=true,
			argName="warning",
			optionalArgs=false,
			option="warning"
			),
			
	@Option(shortName="c",
			longName="critical",
			description="Set CRITICAL status if more than INTEGER users are logged in",
			required=false,
			hasArgs=true,
			argName="critical",
			optionalArgs=false,
			option="critical"),
			
	@Option(shortName="T",
			longName="th",
			description="Configure a threshold. Format : metric={metric},ok={range},warn={range},crit={range},unit={unit},prefix={SI prefix}",
			required=false,
			hasArgs=true,
			argName="critical",
			optionalArgs=false,
			option="th")
})
public class CheckUsers extends PluginBase {
	
    @Override
    public void configureThresholdEvaluatorBuilder(
            ThresholdsEvaluatorBuilder thrb, ICommandLine cl)
            throws BadThresholdException {

        if (cl.hasOption("th")) {
            super.configureThresholdEvaluatorBuilder(thrb, cl);
        } else {
            thrb.withLegacyThreshold("users", null,
                    cl.getOptionValue("warning"), cl.getOptionValue("critical"));
        }

    }

    @Override
    public Collection<Metric> gatherMetrics(ICommandLine cl)
            throws MetricGatheringException {

        List<Metric> metricList = new ArrayList<Metric>();
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("linux")) {
                metricList.add(new Metric("users", "", new BigDecimal(
                        getLinuxLoggedInUsers()), null, null));
            } else if (os.contains("windows")) {
                metricList.add(new Metric("users", "", new BigDecimal(
                        getWindowsLoggedInUsers()), null, null));
            }

            return metricList;
        } catch (IOException e) {
            log.warn("CheckUser plugin execution error: "
                    + e.getMessage(), e);

            throw new MetricGatheringException("An error has occurred : "
                    + e.getMessage(), Status.UNKNOWN, e);
        }
    }

    /**
     * Get list of logged in users for linux.
     *
     * @return The number of logged in users
     * @throws IOException
     *             -
     */
    private int getLinuxLoggedInUsers() throws IOException {
        String command = "/usr/bin/users";
        List<String> users = new ArrayList<String>();
        ProcessBuilder builder = new ProcessBuilder();
        Process proc = null;
        proc = builder.command(command).start();
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
            users.add(line);
        }
        return users.size();
    }

    /**
     * Get list of logged in users for windows by counting the number of
     * explorer.exe processes.
     *
     * @return The number of logged in windows users
     * @throws IOException
     *             -
     */
    private int getWindowsLoggedInUsers() throws IOException {
        String command =
                System.getenv("windir") + "\\system32\\" + "tasklist.exe";
        int userCount = 0;
        ProcessBuilder builder = new ProcessBuilder();
        Process proc = null;
        proc = builder.command(command).start();
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("explorer.exe")) {
                userCount++;
            }
        }
        return userCount;
    }

    @Override
    protected String getPluginName() {
        return "CHECK_USERS";
    }
}
