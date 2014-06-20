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

import java.math.BigDecimal;

/**
 * This class represent a metric gathered from a plugin.
 * 
 * @author Massimiliano Ziccardi
 */
public class Metric {
    /**
     * The name of the metric.
     */
    private final String metricName;

    /**
     * The message associated with this metric. This is the message that will be
     * returned to Nagios if the user requested for this metric.
     */
    private final String metricMessage;

    /**
     * The value of this metric.
     */
    private final BigDecimal metricValue;

    /**
     * The maximum value for this metric.
     */
    private final BigDecimal minValue;

    /**
     * The minimum value for this metric.
     */
    private final BigDecimal maxValue;

    /**
     * Builds and initializes a metric object.
     * 
     * @param name
     *            The name of the metric
     * @param message
     *            The message associated with this metric. For example: disk
     *            usage 50%.
     * @param value
     *            The value of this metric (can't be null)
     * @param min
     *            The minimum value for this metric (can be null)
     * @param max
     *            The maximum value for this metric (can be null)
     */
    public Metric(final String name, final String message, final BigDecimal value, final BigDecimal min, final BigDecimal max) {
        metricName = name;
        metricValue = value;
        minValue = min;
        maxValue = max;
        metricMessage = message;
    }

    /**
     * @return The name of this metric.
     */
    public final String getMetricName() {
        return metricName;
    }

    /**
     * @return The value of this metric.
     */
    public final BigDecimal getMetricValue() {
        return metricValue;
    }

    /**
     * @return The minimum value for this metric.
     */
    public final BigDecimal getMinValue() {
        return minValue;
    }

    /**
     * @return The maximum value for this metric.
     */
    public final BigDecimal getMaxValue() {
        return maxValue;
    }

    /**
     * @return The message associated with this metric.
     */
    public final String getMessage() {
        return metricMessage;
    }
}
