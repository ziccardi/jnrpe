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
package it.jnrpe.plugin.tomcat;

import it.jnrpe.ICommandLine;
import it.jnrpe.JNRPELogger;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricBuilder;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.Prefixes;
import it.jnrpe.utils.thresholds.ReturnValueBuilder;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Plugin(name = "CHECK_TOMCAT", description = "Checks the tomcat /manager/status page for excessive memory usage or an excessive number of threads in use.\n"
        + "This plugin does a HTTP GET of the tomcat status page:\n"
        + "\n"
        + "  /manager/status?XML=true\n"
        + "\n"
        + "  It checks the resulting XML for:\n"
        + "\n"
        + "  * Low free memory\n"
        + "  * Excessive thread usage\n"
        + "\n"
        + "    In order to use this, you must provide a username and password that has a 'manager-gui' role in the\n"
        + "    CATALINA_HOME/conf/tomcat-users.xml file.\n")
@PluginOptions({
        @Option(shortName = "h", longName = "hostname", description = "Hostname or IP address", required = true, hasArgs = true, argName = "hostname", optionalArgs = false, option = "hostname"),
        @Option(shortName = "p", longName = "port", description = "Port number; default is 8080", required = false, hasArgs = true, argName = "port", optionalArgs = false, option = "port"),
        @Option(shortName = "l", longName = "username", description = "Username for authentication", required = true, hasArgs = true, argName = "username", optionalArgs = false, option = "username"),
        @Option(shortName = "a", longName = "password", description = "Password for authentication", required = true, hasArgs = true, argName = "password", optionalArgs = false, option = "password"),
        @Option(shortName = "m", longName = "memory", description = "Check memory", required = false, hasArgs = false, argName = "memory", optionalArgs = false, option = "memory"),
        @Option(shortName = "t", longName = "threads", description = "Check threads", required = false, hasArgs = false, argName = "threads", optionalArgs = false, option = "threads"),
        @Option(shortName = "P", longName = "percent", description = "Check the usage as percen", required = false, hasArgs = false, argName = "percent", optionalArgs = false, option = "percent"),
        
        @Option(shortName = "w", longName = "warning", description = "Warning threshold value for threads or memory (in MB). Must be used with either the 'memory' or 'threads' option.", required = false, hasArgs = true, argName = "warning", optionalArgs = false, option = "warning"),
        @Option(shortName = "c", longName = "critical", description = "Critical threshold value for threads or memory (in MB). Must be used with either the 'memory' or 'threads' option.", required = false, hasArgs = true, argName = "critical", optionalArgs = false, option = "critical"),

        @Option(shortName = "r", longName = "th", description = "Configure a threshold. Format : metric={metric},ok={range},warn={range},crit={range},unit={unit},prefix={SI prefix}", required = false, hasArgs = true, argName = "thresholds", optionalArgs = false, option = "thresholds"),
        @Option(shortName = "S", longName = "ssl", description = "Use ssl", required = false, hasArgs = false, argName = "ssl", optionalArgs = false, option = "ssl"),
        @Option(shortName = "T", longName = "timeout", description = "Connection timeout in seconds. Default is 10.", required = false, hasArgs = true, argName = "timeout", optionalArgs = false, option = "timeout"), 
})
public class CheckTomcat extends PluginBase {

    /**
     * The dataprovider object. Used to retrieve the information from the 
     * Tomcat application server.
     */
    private IAppServerDataProvider dataProvider = new TomcatDataProvider();

    /**
     * The logger object.
     */
    protected final JNRPELogger LOG = new JNRPELogger(this);

    /**
     * Default Tomcat http port.
     */
    private static final String DEFAULT_PORT = "8080";

    /**
     * Default Tomcat manager URL.
     */
    private static final String DEFAULT_URI = "/manager/status?XML=true";

    /**
     * Default timeout.
     */
    private static final String DEFAULT_TIMEOUT = "10";

    @Override
    protected String getPluginName() {
        return "CHECK_TOMCAT";
    }

    /**
     * Computes the percent value of the passed in <code>value</code>.
     * 
     * @param value the value to be converted to a percent value.
     * @param maxValue the maximum value that <code>value</code> can
     * assume
     * 
     * @return the value as percent
     */
    private BigDecimal toPercent(BigDecimal value, BigDecimal maxValue) {
        
        return value.divide(maxValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
    }
    
    /**
     * Computes the percent value of the passed in <code>value</code>.
     * 
     * @param value the value to be converted to a percent value.
     * @param maxValue the maximum value that <code>value</code> can
     * assume
     * 
     * @return the value as percent
     */
    private BigDecimal toPercent(long value, long maxValue) {
        return toPercent(new BigDecimal(value), new BigDecimal(maxValue));
    }
    
    /**
     * Gathers the metrics about the Tomcat JVM memory usage
     * 
     * @return the gathered metrics
     * @throws MetricGatheringException
     */
    private Collection<Metric> gatherMemoryMetric() throws MetricGatheringException {

        List<Metric> res = new ArrayList<Metric>();

        MemoryData jvmMemory = dataProvider.getJVMMemoryUsage();
        
        final BigDecimal freeMemory = Prefixes.RAW.convert(jvmMemory.getFreeMemory(), Prefixes.mega);
        
        BigDecimal percent = toPercent(jvmMemory.getFreeMemory(), jvmMemory.getTotalMemory());
        
        res.add(MetricBuilder.forMetric("memory")
                .withMessage("Memory : {0,number,#.##}mb", freeMemory.doubleValue())
                .withValue(jvmMemory.getFreeMemory(), "#.##")
                .withMinValue(0, "#.##")
                .withMaxValue(jvmMemory.getTotalMemory(), "#.##").build());
        
        LOG.debug(getContext(), "Created metric : memory");
        
        res.add(MetricBuilder.forMetric("memory%")
                .withMessage("Used Memory : {0,number,#.##}%", percent.doubleValue())
                .withValue(percent, "#.##")
                .withMinValue(0, "#")
                .withMaxValue(100, "#").build());

        LOG.debug(getContext(), "Created metric : memory%");
        
        for (MemoryPoolData mpd : dataProvider.getMemoryPoolData()) {
            res.add(MetricBuilder
                    .forMetric(mpd.getPoolName() + "-memoryPool")
                    .withMessage("Memory Pool - usage init: {0}, usage committed: {1}, usage max: {2}, usage used: {3}", 
                            mpd.getUsageInit(),
                            mpd.getUsageCommitted(), 
                            mpd.getUsageMax(), 
                            mpd.getUsageUsed())
                     .withValue(mpd.getUsageUsed(), "#.##")
                     .withMinValue(0, "#.##")
                    .withMaxValue(mpd.getUsageMax(), "#.##").build());
            
            LOG.debug(getContext(), "Created metric : " + mpd.getPoolName() + "-memoryPool");
            
            BigDecimal memoryPoolPercent = toPercent(mpd.getUsageUsed(), mpd.getUsageMax());
            
            res.add(MetricBuilder
                    .forMetric(mpd.getPoolName() + "-memoryPool%")
                    .withMessage("Memory Pool - usage {0,number,#.##}%", memoryPoolPercent)
                    .withValue(memoryPoolPercent, "#.##")
                    .withMinValue(0, "#")
                    .withMaxValue(mpd.getUsageMax(), "#").build());
            LOG.debug(getContext(), "Created metric : " + mpd.getPoolName() + "-memoryPool%");;
        }

        return res;
    }

    /**
     * Gathers the metrics about the thread usage of each connector.
     * The metric name will have the following format: [connector-name]-threadInfo.
     * This way user will be able to configure checks against a single connector.
     * 
     * @return the metrics
     * @throws MetricGatheringException
     */
    private Collection<Metric> gatherConnectorMetrics() throws MetricGatheringException {

        List<Metric> res = new ArrayList<Metric>();

        for (ThreadData td : dataProvider.getThreadData()) {

            final String metricName = td.getConnectorName().replace("\"", "") + "-threadInfo";
            final String percentMetricName = td.getConnectorName().replace("\"", "") + "-threadInfo%";

            res.add(MetricBuilder
                    .forMetric(metricName)
                    .withMessage("{0} - thread count: {1}, current thread busy: {2}, max thread: {3}", metricName, td.getCurrentThreadCount(),
                            td.getCurrentThreadBusy(), td.getMaxThreadCount())
                    .withValue(td.getCurrentThreadBusy()).withMinValue(0)
                    .withMaxValue(td.getMaxThreadCount()).build());
            
            final BigDecimal threadUsagePercent = toPercent(td.getCurrentThreadBusy(), td.getMaxThreadCount());
            
            res.add(MetricBuilder
                    .forMetric(percentMetricName)
                    .withMessage("{0} - thread usage: {1,number,#.##}%", metricName, threadUsagePercent)
                    .withValue(threadUsagePercent, "#.##")
                    .withMinValue(0, "#")
                    .withMaxValue(td.getMaxThreadCount(), "#")
                    .build());
            
            LOG.debug(getContext(), "Created metric : " + metricName);
        }

        return res;
    }

    @Override
    protected Collection<Metric> gatherMetrics(ICommandLine cl) throws MetricGatheringException {

        Collection<Metric> metrics = new ArrayList<Metric>();
        metrics.addAll(gatherConnectorMetrics());
        metrics.addAll(gatherMemoryMetric());

        return metrics;
    }

    /**
     * Internally used to initialize the Tomcat connector.
     * 
     * @param cl the received command line
     * @throws MetricGatheringException
     */
    private void init(ICommandLine cl) throws MetricGatheringException {
        String username = cl.getOptionValue("username");
        String password = cl.getOptionValue("password");
        int timeout = Integer.parseInt(cl.getOptionValue("timeout", DEFAULT_TIMEOUT));
        String hostname = cl.getOptionValue("hostname");

        String port = cl.getOptionValue("port", DEFAULT_PORT);
        String uri = cl.getOptionValue("uri", DEFAULT_URI);
        try {
            this.dataProvider.init(hostname, uri, Integer.parseInt(port), username, password, cl.hasOption("ssl"), timeout);
        } catch (Exception e) {
            throw new MetricGatheringException(e.getMessage(), Status.UNKNOWN, e);
        }
    }
    
    /**
     * Return <code>true</code> if the received metricName is about a memory 
     * metric.
     * 
     * @param metricName the metric to be checked
     * 
     * @return <code>true</code> if the received metricName is about a memory 
     * metric
     */
    private boolean isMemory(String metricName) {
        String tmp = metricName.toLowerCase();
        return tmp.equals("memory") || tmp.equals("memory%");
    }
    
    /**
     * Return <code>true</code> if the received metricName is about a threads 
     * metric.
     * 
     * @param metricName the metric to be checked
     * 
     * @return <code>true</code> if the received metricName is about a threads 
     * metric
     */
    private boolean isThread(String metricName) {
        String tmp = metricName.toLowerCase();
        return tmp.endsWith("-threadInfo") || tmp.endsWith("-threadInfo%");
    }
    
    /**
     * Return <code>true</code> if the received metricName is about a percent 
     * metric.
     * 
     * @param metricName the metric to be checked
     * 
     * @return <code>true</code> if the received metricName is about a percent 
     * metric
     */
    private boolean isPercent(String metricName) {
        return metricName.endsWith("%");
    }
    
    /**
     * Returns the prefix to be used to create the metrics.
     * 
     * @param cl the received command line
     * 
     * @return the prefix to be used. Memory metrics are produced as 
     * {@link Prefixes#mega}, while percent metrics are {@link Prefixes#RAW}.
     */
    private Prefixes getPrefix(ICommandLine cl) {
        
        if (cl.hasOption("memory") && !cl.hasOption("percent")) {
            return Prefixes.mega;
        }
        
        return Prefixes.RAW;
    }
    
    /**
     * Update the return value of this plugin.
     * 
     * @param oldRet
     * @param ret
     * @return
     */
    private ReturnValue updateRet(ReturnValue oldRet, ReturnValue ret) {
        if (oldRet == null) {
            return ret;
        }
        
        if (ret.getStatus().getSeverity() > oldRet.getStatus().getSeverity()) {
            return ret;
        }
        
        return oldRet;
    }
    
    @Override
    public ReturnValue execute(ICommandLine cl) throws BadThresholdException {
        try{
            init(cl);
            
            // If we are not using the new threshold format, we have to manually handle the 
            // range checks.
            if (!cl.hasOption("th")) {
                String criticalThreshold = cl.getOptionValue("critical");
                String warningThreshold = cl.getOptionValue("warning");
                
                Collection<Metric> metrics;
                
                if (cl.hasOption("memory")) {
                    metrics = gatherMemoryMetric();
                } else if (cl.hasOption("threads")) {
                    metrics = gatherConnectorMetrics();
                } else {
                    return ReturnValueBuilder
                            .forPlugin(getPluginName())
                            .withStatus(Status.OK)
                            .create();
                }
                
                ReturnValue ret = null;

                for (Metric metric : metrics) {
                    
                    if (cl.hasOption("memory") && !isMemory(metric.getMetricName())) {
                        continue;
                    }
                    
                    if (cl.hasOption("thread") && !isThread(metric.getMetricName())) {
                        continue;
                    }
                    
                    if (cl.hasOption("percent") && !isPercent(metric.getMetricName())) {
                        continue;
                    }
                    
                    ret = updateRet(ret, ReturnValueBuilder.forPlugin(
                            getPluginName(),
                            new ThresholdsEvaluatorBuilder()
                                .withLegacyThreshold(metric.getMetricName(), null, warningThreshold, criticalThreshold, getPrefix(cl))
                                .create())
                            .withValue(metric)
                            .create());
                }
                
                if (ret != null) {
                    return ret;
                }
                
                return ReturnValueBuilder.forPlugin(this.getPluginName())
                        .withForcedMessage("No metrics gathered")
                        .withStatus(Status.UNKNOWN).create();
                
            } else {
                // For the new threshold format, we don't have anything to do
                return super.execute(cl);
            }
        } catch (MetricGatheringException mge) {
            LOG.info(getContext(), "Plugin execution failed : " + mge.getMessage(), mge);
            return ReturnValueBuilder.forPlugin(getPluginName()).withForcedMessage(mge.getMessage()).withStatus(mge.getStatus()).create();
        }
    }
}
