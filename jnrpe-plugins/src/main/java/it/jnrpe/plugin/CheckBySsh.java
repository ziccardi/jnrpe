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
import it.jnrpe.plugin.utils.SshUtils;
import it.jnrpe.plugin.utils.Utils;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This plugins executes remote commands through ssh and returns the command's
 * output (if successul).
 * 
 * @author Frederico Campos
 * 
 */
@Plugin(name = "CHECK_BY_SSH", description = "Use ssh to execute commands on a remote host.\n" + "EXAMPLES:\n"
        + "The example will be based upon the following command definition (ini file)\n\n"
        + "check_by_ssh : CHECK_BY_SSH --hostname $ARG1$ --port $ARG2$ --password $ARG3$ -C $ARG4$\n"
        + "check_nrpe -H myjnrpeserver -c check_ssh -a myhostname 22 password uptime")
@PluginOptions({
        @Option(shortName = "h", longName = "hostname", description = "IP or hostname", required = true, hasArgs = true, argName = "hostname", optionalArgs = false, option = "hostname"),
        @Option(shortName = "p", longName = "port", description = "Port number. Default is 22.", required = false, hasArgs = true, argName = "port", optionalArgs = false, option = "port"),
        @Option(shortName = "u", longName = "username", description = "Username.", required = true, hasArgs = true, argName = "username", optionalArgs = false, option = "username"),
        @Option(shortName = "P", longName = "password", description = "Password.", required = true, hasArgs = true, argName = "hostname", optionalArgs = false, option = "password"),
        @Option(shortName = "t", longName = "timeout", description = "Seconds before connection times out (default: 10)", required = false, hasArgs = true, argName = "timeout", optionalArgs = false, option = "timeout"),
        @Option(shortName = "w", longName = "warning", description = "Response time to result in warning status (seconds)", required = false, hasArgs = true, argName = "warning", optionalArgs = false, option = "warning"),
        @Option(shortName = "c", longName = "critical", description = "Response time to result in critical status (seconds)", required = false, hasArgs = true, argName = "critical", optionalArgs = false, option = "critical"),
        @Option(shortName = "C", longName = "command", description = "command to execute on the remote machine", required = true, hasArgs = true, argName = "command", optionalArgs = false, option = "command") })
public class CheckBySsh extends PluginBase {

    /**
     * Possible unix return codes
     */
    /**
     * command not found
     */
    private static final int ERR_CMD_NOT_FOUND = 127;

    /**
     * no permision
     */
    private static final int ERR_NO_PERMISSION = 126;

    @Override
    protected String getPluginName() {
        return "CHECK_BY_SSH";
    }

    @Override
    public final void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {
        thrb.withLegacyThreshold("session", "1", null, "0");
        thrb.withLegacyThreshold("response", null, cl.getOptionValue("warning"), cl.getOptionValue("critical"));
        thrb.withLegacyThreshold("result", "1", null, "0");

    }

    @Override
    public final Collection<Metric> gatherMetrics(final ICommandLine cl) throws MetricGatheringException {
        List<Metric> metrics = new ArrayList<Metric>();
        Session session = null;
        Channel channel = null;
        String command = cl.getOptionValue("command");
        InputStream in = null;
        boolean hasSession = false;
        long then = System.currentTimeMillis();
        try {
            session = SshUtils.getSession(cl);
            channel = session.openChannel("exec");

            hasSession = true;
            metrics.add(new Metric("session", "", new BigDecimal(1), null, null));
        } catch (Exception e) {
            // metrics.add(new Metric("session",
            // "SSH not started, permission denied. " + e.getMessage(),
            // new BigDecimal(0), null, null));
            LOG.debug(getContext(), e.getMessage(), e);
            throw new MetricGatheringException("SSH not started, permission denied.", Status.UNKNOWN, e);
        }
        try {
            if (hasSession) {
                ((ChannelExec) channel).setCommand(command);
                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);
                in = channel.getInputStream();
            } else {
                return metrics;
            }
        } catch (IOException e1) {
            // e1.printStackTrace();
            throw new MetricGatheringException(e1.getMessage(), Status.UNKNOWN, e1);
        }

        try {
            channel.connect();
            metrics.add(new Metric("connected", "", new BigDecimal(1), null, null));
        } catch (JSchException e2) {
            // e2.printStackTrace();
            throw new MetricGatheringException(e2.getMessage(), Status.UNKNOWN, e2);
        }

        StringBuilder sb = new StringBuilder();
        byte[] tmp = new byte[1024];
        int exitStatus = 0;
        while (true) {
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    sb.append(new String(tmp, 0, i, "UTF-8"));
                }
            } catch (IOException e1) {
                throw new MetricGatheringException(e1.getMessage(), Status.UNKNOWN, e1);
            }
            if (channel.isClosed()) {
                exitStatus = channel.getExitStatus();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                LOG.error(getContext(), "gatherMetrics - " + e.getMessage(), e);
            }
        }
        if (channel != null) {
            channel.disconnect();
        }

        session.disconnect();
        long response = (System.currentTimeMillis() - then) / 1000;
        metrics.add(new Metric("response", "", new BigDecimal(response), null, null));
        // sb.append("\nexit-status: " + channel.getExitStatus());
        String msg = "";
        switch (channel.getExitStatus()) {
        case ERR_CMD_NOT_FOUND:
            msg = "Command not found.";
            break;
        case ERR_NO_PERMISSION:
            msg = "Not enough permission to execute command.";
            break;
        default:
            break;
        }

        metrics.add(new Metric("result", msg + " " + sb.toString(), new BigDecimal(Utils.getIntValue(exitStatus == 0)), null, null));
        return metrics;
    }
}