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

import it.jnrpe.utils.thresholds.Prefixes;

import java.math.BigDecimal;

/**
 * This class represent a metric gathered from a plugin.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class Metric {
    
    private Prefixes prefix = Prefixes.RAW;
    
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
    private final MetricValue metricValue;

    /**
     * The maximum value for this metric.
     */
    private final MetricValue minValue;

    /**
     * The minimum value for this metric.
     */
    private final MetricValue maxValue;

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
        metricValue = convert(value);
        minValue = convert(min);
        maxValue = convert(max);
        metricMessage = message;
    }

    Metric(final String name, final String message, final BigDecimal value, final BigDecimal min, final BigDecimal max, final Prefixes prefix) {
        metricName = name;
        metricValue = convert(value);
        minValue = convert(min);
        maxValue = convert(max);
        metricMessage = message;
        this.prefix = prefix;
    }
    
    private static MetricValue convert(Number val) {
        if (val == null) {
            return null;
        }
        
        if (val instanceof BigDecimal) {
            return MetricValue.valueOf((BigDecimal) val);
        }
        
        return new MetricValue(val.toString());
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
    public final MetricValue getMetricValue() {
        return metricValue;
    }

    public final MetricValue getMetricValue(Prefixes outputPrefix) {
        return convert(prefix.convert(metricValue, outputPrefix));
    }
    
    /**
     * @return The minimum value for this metric. 
     */
    public final MetricValue getMinValue() {
        return minValue;
    }
    
    public final MetricValue getMinValue(Prefixes outputPrefix) {
        return convert(prefix.convert(minValue, outputPrefix));
    }

    /**
    
     * @return The maximum value for this metric. */
    public final MetricValue getMaxValue() {
        return maxValue;
    }
    
    public final MetricValue getMaxValue(Prefixes outputPrefix) {
        return convert(prefix.convert(maxValue, outputPrefix));
    }

    /**
     * @return The message associated with this metric. 
     */
    public final String getMessage() {
        return metricMessage;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "Metric [metricName=" + metricName + ", metricMessage=" + metricMessage + ", metricValue=" + metricValue + ", minValue=" + minValue
                + ", maxValue=" + maxValue + "]";
    }
}
