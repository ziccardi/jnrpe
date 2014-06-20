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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Checks statistics for a remote host
 * 
 * @author Frederico Campos
 * 
 */

@Plugin(name = "CHECK_PING",

description = "Checks connection statistics for a remote host.\n\n"
        + "Usage:\n\n"
        + "The example will be based upon the following command definition (ini file)\n\n"
        + "check_ssh : CHECK_PING --hostname $ARG1$ --critical $ARG2$ \n"
        + "check_nrpe -H myjnrpeserver -c check_ping -a myhostname 3000,50%:\n\n"
        + "THRESHOLD is <rta>,<pl>% where <rta> is the round trip average travel time (ms) which triggers a WARNING or CRITICAL state, and <pl> is the \n"
        + "percentage of packet loss to trigger an alarm state.\n" + "Note: a root privilege is required"

)
@PluginOptions({
        @Option(shortName = "H", longName = "hostname", description = "Hostname or ip address to ping", required = true, hasArgs = true, argName = "hostname", optionalArgs = false, option = "hostname"),
        @Option(shortName = "p", longName = "packets", description = "number of ICMP ECHO packets to send (Default: 5)", required = false, hasArgs = true, argName = "packets", optionalArgs = false, option = "packets"),
        @Option(shortName = "4", longName = "use-ipv4", description = "Use IPv4.", required = false, hasArgs = false, argName = "use-ipv4", optionalArgs = false, option = "use-ipv4"),
        @Option(shortName = "6", longName = "use-ipv6", description = "Use IPv6.", required = false, hasArgs = false, argName = "use-ipv6", optionalArgs = false, option = "use-ipv6"),
        @Option(shortName = "c", longName = "critical", description = "critical threshold pair", required = false, hasArgs = true, argName = "critical", optionalArgs = false, option = "critical"),
        @Option(shortName = "w", longName = "warning", description = "warning threshold pair", required = false, hasArgs = true, argName = "warning", optionalArgs = false, option = "warning"),
        @Option(shortName = "t", longName = "timeout", description = "Seconds before connection times out (default: 10)", required = false, hasArgs = true, argName = "timeout", optionalArgs = false, option = "timeout") })
public class CheckPing extends PluginBase {

    /**
     * Default timeout.
     */
    private static final int DEFAULT_TIMEOUT = 10;

    /**
     * Default packets
     */
    private static final int DEFAULT_PACKETS = 5;

    private static final String RTA = "round trip average";

    private static final String PACKET_LOSS = "packet loss";

    @Override
    protected String getPluginName() {
        return "CheckPing";
    }

    public final void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {
        String critical = cl.getOptionValue("critical");
        String warning = cl.getOptionValue("warning");

        String rtaCritical = getValue(0, critical);
        String rtaWarning = getValue(0, warning);
        String plCritical = getValue(1, critical);
        String plWarning = getValue(1, warning);

        if (rtaCritical != null || rtaWarning != null) {
            thrb.withLegacyThreshold(RTA, null, rtaWarning, rtaCritical);
        }

        if (plCritical != null || plWarning != null) {
            thrb.withLegacyThreshold(PACKET_LOSS, null, plWarning, plCritical);
        }

    }

    private String getValue(int index, String str) {
        if (str == null) {
            return null;
        }
        String arr[] = str.split(",");
        if (arr.length >= index) {
            if (arr[index].equals("")) {
                return null;
            }
            String val = arr[index].trim().replace("%", "");
            if (val.indexOf(":") < 0) {
                val = val + ":";
            }
            return val;
        }
        return null;
    }

    public Collection<Metric> gatherMetrics(ICommandLine cl) throws MetricGatheringException {
        List<Metric> metrics = new ArrayList<Metric>();

        String hostname = cl.getOptionValue("hostname");
        int timeout = DEFAULT_TIMEOUT;
        if (cl.getOptionValue("timeout") != null) {
            timeout = Integer.parseInt(cl.getOptionValue("timeout"));
        }
        int packets = DEFAULT_PACKETS;
        if (cl.getOptionValue("packets") != null) {
            packets = Integer.parseInt(cl.getOptionValue("packets"));
        }

        double roundTripAvg = 0;
        int packetLossPerc = 0;
        long time = 0;
        long packetLoss = 0;
        for (int pings = 0; pings < packets; pings++) {
            try {
                InetAddress inet = null;
                if (cl.hasOption("ipv6")) {
                    inet = Inet6Address.getByName(hostname);
                } else if (cl.hasOption("ipv4")) {
                    inet = Inet4Address.getByName(hostname);
                } else {
                    inet = InetAddress.getByName(hostname);
                }
                boolean reachable = false;
                long then = System.currentTimeMillis();
                reachable = inet.isReachable(timeout * 1000);
                time += (System.currentTimeMillis() - then);
                if (!reachable) {
                    packetLoss++;
                }
            } catch (UnknownHostException e) {
                throw new MetricGatheringException(e.getMessage(), Status.CRITICAL, e);
            } catch (IOException e) {
                throw new MetricGatheringException(e.getMessage(), Status.CRITICAL, e);
            }
        }

        roundTripAvg = (double) time / packets;
        packetLossPerc = (int) (packetLoss / packets) * 100;

        metrics.add(new Metric(RTA, "", new BigDecimal(roundTripAvg), null, null));
        metrics.add(new Metric(PACKET_LOSS, "", new BigDecimal(packetLossPerc), null, null));

        return metrics;
    }

}
