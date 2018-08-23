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
package it.jnrpe.utils.thresholds;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;

import org.apache.commons.lang.StringUtils;

/**
 * This object takes the responsability to build and configure the return value
 * object and the performance data. The plugin has only the responsability to
 * gain the metrics and pass them to the builder: both status and performance
 * data will be generated.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public final class ReturnValueBuilder {
    /**
     * The return value that we are configuring.
     */
    private final ReturnValue retVal = new ReturnValue().withStatus(Status.OK);

    /**
     * The thresholds that must be used to compute the Status result.
     */
    private final ThresholdsEvaluator thresholds;

    /**
     * The status.
     */
    private Status status = Status.OK;

    /**
     * The status to force.
     */
    private Status forcedStatus = null;

    /**
     * The name of the plugin that is generating this result.
     */
    private final String pluginName;

    /**
     * The value of the message to return to Nagios.
     */
    private String retValMessage = "";

    /**
     * Constructs the object passing the thresholds evaluator.
     * 
     * @param name
     *            The name of the plugin that is creating this result
     * @param thr
     *            The threshold evaluator.
     */
    private ReturnValueBuilder(final String name, final ThresholdsEvaluator thr) {
        pluginName = name;
        thresholds = thr;
    }

    /**
     * Constructs the object with an empty threshold evaluator.
     * 
     * @param name
     *            The name of the plugin that is creating this result
    
     * @return the newly created instance. */
    public static ReturnValueBuilder forPlugin(final String name) {
        return forPlugin(name, null);
    }

    /**
     * Constructs the object with the given threshold evaluator.
     * 
     * @param name
     *            The name of the plugin that is creating this result
     * @param thr
     *            The threshold evaluator.
    
     * @return the newly created instance. */
    public static ReturnValueBuilder forPlugin(final String name, final ThresholdsEvaluator thr) {
        if (thr != null) {
            return new ReturnValueBuilder(name, thr);
        }
        return new ReturnValueBuilder(name, new ThresholdsEvaluatorBuilder().create());
    }

    /**
     * Configure the {@link ReturnValue} we are building with the specified
     * metric.
     * 
     * @param pluginMetric
     *            The metric for which we want to compute the result Status.
    
     * @return this */
    public ReturnValueBuilder withValue(final Metric pluginMetric) {
        if (thresholds.isMetricRequired(pluginMetric.getMetricName())) {
            Status newStatus = thresholds.evaluate(pluginMetric);
            if (newStatus.getSeverity() > status.getSeverity()) {
                status = newStatus;
            }

            IThreshold thr = thresholds.getThreshold(pluginMetric.getMetricName());

            formatResultMessage(pluginMetric);

            retVal.withPerformanceData(pluginMetric, thr);
        }
        return this;
    }

    /**
     * Formats the message to return to Nagios according to the specifications
     * contained inside the pluginMetric object.
     * 
     * @param pluginMetric
     *            The metric.
     */
    private void formatResultMessage(final Metric pluginMetric) {
        if (StringUtils.isEmpty(pluginMetric.getMessage())) {
            return;
        }

        if (StringUtils.isEmpty(retValMessage)) {
            retValMessage = pluginMetric.getMessage();
            return;
        }
        retValMessage += " " + pluginMetric.getMessage();
    }

    /**
     * Force the message to return to Nagios. Instead of computing the message
     * using the {@link Metric} object received in the
     * {@link #withValue(Metric)} methods, this value will be returned.
     * 
     * @param message
     *            The message to return.
     * 
     * @return this 
     */
    public ReturnValueBuilder withForcedMessage(final String message) {
        retValMessage = message;
        return this;
    }

    /**
     * Use this method if you want to force the status to be returned. This can
     * be useful if, for example, your check got an error and has no metric to
     * be evaluated to automatically compute the status.
     * 
     * @param forceStatus
     *            The status to be forced
     * 
     * @return this 
     */
    public ReturnValueBuilder withStatus(final Status forceStatus) {
        forcedStatus = forceStatus;
        return this;
    }

    /**
     * Builds the configured {@link ReturnValue} object.
     * 
     * @return The {@link ReturnValue} object 
     */
    public ReturnValue create() {
        if (forcedStatus == null) {
            retVal.withStatus(status);
        } else {
            retVal.withStatus(forcedStatus);
        }

        StringBuilder msg = new StringBuilder(pluginName).append(" : ").append(retVal.getStatus());
        if (!StringUtils.isEmpty(retValMessage)) {
            msg.append(" - ").append(retValMessage);
        }

        retVal.withMessage(msg.toString());

        return retVal;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "ReturnValueBuilder [retVal=" + retVal + ", thresholds=" + thresholds + ", status=" + status + ", forcedStatus=" + forcedStatus
                + ", pluginName=" + pluginName + ", retValMessage=" + retValMessage + "]";
    }
}
