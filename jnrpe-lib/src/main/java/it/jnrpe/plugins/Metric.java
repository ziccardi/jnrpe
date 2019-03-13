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
import java.text.MessageFormat;

/**
 * This class represent a metric gathered from a plugin.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class Metric {
    private String name;
    private BigDecimal value;
    private String warningThreshold;
    private String criticalThreshold;
    private BigDecimal min;
    private BigDecimal max;

    private String message;

    private String UOM = "";

    private BigDecimal outputMetric;

    private Metric() {
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public String getCriticalThreshold() {
        return criticalThreshold;
    }

    public String getWarningThreshold() {
        return warningThreshold;
    }

    public BigDecimal getOutputMetric() {
        return this.outputMetric != null ? this.outputMetric : this.value;
    }

    public String getUOM() {
        return UOM;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static class MetricBuilder<K extends Comparable> {
        private Metric metric = new Metric();

        private MetricBuilder(String metricName) {
            this.metric.name = metricName;
        }

        public MetricBuilder<K> withValue(K value) {
            if (value == null) {
                throw new IllegalArgumentException("Metric value can't be null");
            }
            this.metric.value = new BigDecimal(value.toString());
            return this;
        }

        public MetricBuilder<K> withOutputMetric(K value, String UOM) {
            this.metric.outputMetric = new BigDecimal(value.toString());
            this.metric.UOM = UOM;
            return this;
        }

        public MetricBuilder<K> withOutputMetric(K value) {
            return this.withOutputMetric(value, null);
        }

        public MetricBuilder<K> withMessage(String message) {
            this.metric.message = message;
            return this;
        }

        public MetricBuilder<K> withMessage(String format, Object...params) {
            this.metric.message = MessageFormat.format(format, params);
            return this;
        }

        public MetricBuilder<K> withWarningThrehold(String threhold) {
            this.metric.warningThreshold = threhold;
            return this;
        }

        public MetricBuilder<K> withCriticalThrehold(String threhold) {
            this.metric.criticalThreshold = threhold;
            return this;
        }

        public MetricBuilder<K> withMinValue(K min) {
            this.metric.min = new BigDecimal(min.toString());
            return this;
        }

        public MetricBuilder<K> withMaxValue(K max) {
            this.metric.max = new BigDecimal(max.toString());;
            return this;
        }

        public Metric build() {
            return this.metric;
        }
    }

    public static<K extends Comparable> MetricBuilder<K> forMetric(String name, Class<K>type) {
        if (name == null) {
            throw new IllegalArgumentException("Metric name can't be null");
        }
        return new MetricBuilder<>(name);
    }
}
