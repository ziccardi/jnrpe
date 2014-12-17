package it.jnrpe.plugins;

import it.jnrpe.utils.thresholds.Prefixes;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class MetricBuilder {

    private final String metricName;
    private BigDecimal current = null;

    private BigDecimal min = null;
    private BigDecimal max = null;

    private String metricMessage = null;
    
    private Prefixes prefix = Prefixes.RAW;
    
    private MetricBuilder(final String metricName) {
        if (metricName == null) {
            throw new NullPointerException ("Metric name can't be null");
        }
        this.metricName = metricName;
    }

    public static MetricBuilder forMetric(String metricName) {
        return new MetricBuilder(metricName);
    }

    public MetricBuilder withValue(Number value) {
        current = new MetricValue(value.toString());            
        return this;
    }
    
    public MetricBuilder withValue(Number value, String prettyPrintFormat) {
        current = new MetricValue(value.toString(), prettyPrintFormat);            
        return this;
    }
    
    public MetricBuilder withMinValue(Number value) {
        min = new MetricValue(value.toString());            
        return this;
    }
    
    public MetricBuilder withMinValue(Number value, String prettyPrintFormat) {
        min = new MetricValue(value.toString(), prettyPrintFormat);            
        return this;
    }
    
    public MetricBuilder withMaxValue(Number value) {
        max = new MetricValue(value.toString());    
        return this;
    }
    
    public MetricBuilder withMaxValue(Number value, String prettyPrintFormat) {
        max = new MetricValue(value.toString(), prettyPrintFormat);    
        return this;
    }
    
    public MetricBuilder withPrefix(Prefixes prefix) {
        this.prefix = prefix;
        return this;
    }
    
    public MetricBuilder withMessage(String message) {
        this.metricMessage = message;
        return this;
    }
    
    public MetricBuilder withMessage(String messagePattern, Object ...params) {
        this.metricMessage = MessageFormat.format(messagePattern, params);
        return this;
    }
    
    public Metric build() {
        return new Metric(metricName, metricMessage, current, min, max, prefix);
    }
}
