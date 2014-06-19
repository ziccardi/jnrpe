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
import it.jnrpe.utils.Elapsed;
import it.jnrpe.utils.TimeUnit;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.time.TimeTCPClient;
import org.apache.commons.net.time.TimeUDPClient;

/**
 * Checks the time on a specified host.
 * 
 * @author Frederico Campos
 */

@Plugin(name = "CHECK_TIME", description = "Checks time on a specified host.")
@PluginOptions({
        @Option(shortName = "H", longName = "hostname", description = "Host name or IP Address", required = true, hasArgs = true, argName = "hostname", optionalArgs = false, option = "hostname"),
        @Option(shortName = "p", longName = "port", description = "Port number (default is 37)", required = false, hasArgs = true, argName = "port", optionalArgs = false, option = "port"),
        @Option(shortName = "u", longName = "udp", description = "Use udp instead of tcp to connect.", required = false, hasArgs = false, argName = "udp", optionalArgs = false, option = "udp"),
        @Option(shortName = "c", longName = "critical-variance", description = "Time difference (sec.) necessary to result in a critical status", required = false, hasArgs = true, argName = "critical-variance", optionalArgs = false, option = "critical-variance"),
        @Option(shortName = "w", longName = "warning-variance", description = "Time difference (sec.) necessary to result in a warning status", required = false, hasArgs = true, argName = "warning", optionalArgs = false, option = "warning"),
        @Option(shortName = "C", longName = "critical-connect", description = "Return critical if elapsed time exceeds value. Default off", required = false, hasArgs = true, argName = "critical", optionalArgs = false, option = "critical"),
        @Option(shortName = "W", longName = "warning-connect", description = "Return warning if elapsed time exceeds value. Default off", required = false, hasArgs = true, argName = "warning", optionalArgs = false, option = "warning"),
        @Option(shortName = "t", longName = "timeout", description = "Seconds before connection times out (default: 10)", required = false, hasArgs = true, argName = "timeout", optionalArgs = false, option = "timeout"), })
public class CheckTime extends PluginBase {

    private static final int DEFAULT_TIMEOUT = 10;

    private static final int DEFAULT_PORT = 37;

    @Override
    protected final String getPluginName() {
        return "CHECK_TIME";
    }

    @Override
    protected final void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl)
            throws BadThresholdException {
        String criticalConnect = cl.getOptionValue("critical-connect");
        String warningConnect = cl.getOptionValue("warning-connect");
        thrb.withLegacyThreshold("time", null, warningConnect, criticalConnect);

        String critical = cl.getOptionValue("critical-variance");
        String warning = cl.getOptionValue("warning-variance");
        thrb.withLegacyThreshold("offset", null, warning, critical);

    }

    @Override
    protected final Collection<Metric> gatherMetrics(final ICommandLine cl) throws MetricGatheringException {
        List<Metric> metrics = new ArrayList<Metric>();
        String host = cl.getOptionValue("hostname");
        int timeout = DEFAULT_TIMEOUT;
        if (cl.getOptionValue("timeout") != null) {
            timeout = Integer.parseInt(cl.getOptionValue("timeout"));
        }
        int port = DEFAULT_PORT;
        if (cl.getOptionValue("port") != null) {
            port = Integer.parseInt(cl.getOptionValue("port"));
        }
        try {
            Date date = null;
            long then = System.currentTimeMillis();
            if (cl.hasOption("udp")) {
                date = getTimeUDP(host, timeout);
            } else {
                date = getTimeTCP(host, timeout, port);
            }
            long elapsed = (System.currentTimeMillis() - then);

            Date now = new Date();

            analyze(metrics, elapsed, now, date);

        } catch (IOException e) {
            throw new MetricGatheringException(e.getMessage(), Status.CRITICAL, e);
        }

        return metrics;
    }

    /**
     * Analizes the data and produces the metrics.
     * 
     * @param metrics produced metrics
     * @param elapsed elapsed time
     * @param now date as of now
     * @param date
     */
    private void analyze(final List<Metric> metrics, final long elapsed, final Date now, final Date date) {
        long diff = 0;

        boolean behind = false;
        if (now.before(date)) {
            behind = true;
            diff = date.getTime() - now.getTime();
        } else if (now.after(date)) {
            diff = now.getTime() - date.getTime();
        }

        Elapsed elapsedTime = new Elapsed(diff, TimeUnit.MILLISECOND);
        
        String msg = getMessage(elapsedTime);
        if (diff > TimeUnit.SECOND.convert(1)) {
            if (behind) {
                msg += "behind";
            } else {
                msg += "ahead";
            }
        }
        metrics.add(new Metric("offset", msg, new BigDecimal(TimeUnit.MILLISECOND.convert(diff, TimeUnit.SECOND)), null, null));
        metrics.add(new Metric("time", "", new BigDecimal(TimeUnit.MILLISECOND.convert(elapsed, TimeUnit.SECOND)), null, null));
    }

    private String getMessage(Elapsed elapsed) {
        String msg = elapsed.getSeconds() + " seconds ";

        if (elapsed.getMinutes() > 0) {
            msg += elapsed.getMinutes() + " minutes ";
        }
        if (elapsed.getHours() > 0) {
            msg += elapsed.getHours() + " hours ";
        }
        if (elapsed.getDays() > 0) {
            msg += elapsed.getDays() + " days ";
        }
        return msg + "difference ";
    }

    private Date getTimeTCP(String host, int timeout, int port) throws IOException {
        TimeTCPClient client = new TimeTCPClient();
        client.setDefaultPort(port);
        client.setDefaultTimeout((int) TimeUnit.SECOND.convert(timeout));
        client.connect(host);
        Date date = client.getDate();
        client.disconnect();
        return date;

    }

    private Date getTimeUDP(String host, int timeout) throws IOException {
        TimeUDPClient client = new TimeUDPClient();
        client.setDefaultTimeout((int) TimeUnit.SECOND.convert(timeout));
        client.open();
        Date date = client.getDate(InetAddress.getByName(host));
        client.close();
        return date;
    }
}
