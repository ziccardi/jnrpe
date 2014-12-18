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
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * This object must be used to build the metric values.
 * 
 * @author Massimiliano Ziccardi
 */
public class MetricBuilder {

    private final String metricName;
    private BigDecimal current = null;

    private BigDecimal min = null;
    private BigDecimal max = null;

    private String metricMessage = null;
    
    private Prefixes prefix = Prefixes.RAW;
    
    /**
     * Constructs a metric builder for the given metric.
     * 
     * @param metricName
     */
    private MetricBuilder(final String metricName) {
        if (metricName == null) {
            throw new NullPointerException ("Metric name can't be null");
        }
        this.metricName = metricName;
    }

    /**
     * Returns a metric builder for the given metric.
     * 
     * @param metricName The metric
     * @return the metric builder
     */
    public static MetricBuilder forMetric(final String metricName) {
        return new MetricBuilder(metricName);
    }

    /**
     * Sets the value of the metric to be built.
     * 
     * @param value the value of the metric
     * @return this
     */
    public MetricBuilder withValue(Number value) {
        current = new MetricValue(value.toString());            
        return this;
    }
    
    /**
     * Sets the value of the metric to be built.
     * 
     * @param value the value of the metric
     * @param prettyPrintFormat the format of the output (@see {@link DecimalFormat})
     * @return this
     */
    public MetricBuilder withValue(Number value, String prettyPrintFormat) {
        current = new MetricValue(value.toString(), prettyPrintFormat);            
        return this;
    }
    
    /**
     * Sets the minimum value of the metric to be built.
     * 
     * @param the minimum value of the metric
     * @return this
     */
    public MetricBuilder withMinValue(Number value) {
        min = new MetricValue(value.toString());            
        return this;
    }
    
    /**
     * Sets the minimum value of the metric to be built.
     * 
     * @param the minimum value of the metric
     * @param prettyPrintFormat the format of the output (@see {@link DecimalFormat})
     * @return this
     */
    public MetricBuilder withMinValue(Number value, String prettyPrintFormat) {
        min = new MetricValue(value.toString(), prettyPrintFormat);            
        return this;
    }
    
    /**
     * Sets the maximum value of the metric to be built.
     * 
     * @param the maximum value of the metric
     * @return this
     */
    public MetricBuilder withMaxValue(Number value) {
        max = new MetricValue(value.toString());    
        return this;
    }
    
    /**
     * Sets the maximum value of the metric to be built.
     * 
     * @param the maximum value of the metric
     * @param prettyPrintFormat the format of the output (@see {@link DecimalFormat})
     * @return this
     */
    public MetricBuilder withMaxValue(Number value, String prettyPrintFormat) {
        max = new MetricValue(value.toString(), prettyPrintFormat);    
        return this;
    }
    
    /**
     * Sets the prefix to be used to interpret this metric value.
     * 
     * @param prefix the prefix to be used to interpret this metric value
     * @return this
     */
    public MetricBuilder withPrefix(Prefixes prefix) {
        this.prefix = prefix;
        return this;
    }
    
    /**
     * Sets the message to be associated with this metric.
     * 
     * @param prefix the message to be associated with this metric
     * @return this
     */
    public MetricBuilder withMessage(String message) {
        this.metricMessage = message;
        return this;
    }
    
    /**
     * Sets the message to be associated with this metric.
     * 
     * @param prefix the message to be associated with this metric
     * @param params the parameter to pass to {@link MessageFormat} to 
     * create the complete message
     * @return this
     */
    public MetricBuilder withMessage(String messagePattern, Object ...params) {
        this.metricMessage = MessageFormat.format(messagePattern, params);
        return this;
    }
    
    /**
     * Build the metric object.
     * 
     * @return the metric object
     */
    public Metric build() {
        return new Metric(metricName, metricMessage, current, min, max, prefix);
    }
}
