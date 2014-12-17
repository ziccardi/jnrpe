package it.jnrpe.plugins;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MetricValue extends BigDecimal {

    /**
     * 
     */
    private static final long serialVersionUID = -3928606988681283898L;

    private static final String DEFAULT_FORMAT = "0.000000";

    private final DecimalFormat DECIMAL_FORMAT;

    public MetricValue(double val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    public MetricValue(double val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }

    public MetricValue(int val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    public MetricValue(int val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }

    public MetricValue(long val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    public MetricValue(long val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }

    public MetricValue(String val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    public MetricValue(String val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }
    
    private MetricValue(BigDecimal var, DecimalFormat df) {
        super(var.toPlainString());
        DECIMAL_FORMAT = df;
    }

    @Override
    public BigDecimal multiply(BigDecimal multiplicand) {
        MetricValue ret = new MetricValue(super.multiply(multiplicand), DECIMAL_FORMAT);
        return ret;
    }
    
    @Override
    public BigDecimal divide(BigDecimal divisor) {
        MetricValue ret = new MetricValue(super.divide(divisor), DECIMAL_FORMAT);
        return ret;
    }
    
//    private static ThreadLocal<Boolean> standardToString = new ThreadLocal<Boolean>();
    
//    @Override
//    public String toString() {
//        if (standardToString.get() != null) {
//            return super.toString();
//        }
//        standardToString.set(Boolean.TRUE);
//        return DECIMAL_FORMAT.format(this);
//    }
    
    public String toPrettyPrintedString() {
        return DECIMAL_FORMAT.format(this);
    }
}
