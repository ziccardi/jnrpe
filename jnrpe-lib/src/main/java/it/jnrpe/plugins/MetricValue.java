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
import java.text.DecimalFormat;

/**
 * The MetricValue object just adds the ability to retrieve
 * the value as formatted string to the {@link BigDecimal} object
 * 
 * @author Massimiliano Ziccardi
 *
 */
public class MetricValue extends BigDecimal {

    /**
     * 
     */
    private static final long serialVersionUID = -3928606988681283898L;

    /**
     * Default decimal format pattern.
     */
    private static final String DEFAULT_FORMAT = "0.000000";

    /**
     * Decimal format used to format the value.
     */
    private final DecimalFormat DECIMAL_FORMAT;

    /**
     * Constructs a MetricValue object starting from a double value
     * and initialize the formatter with the {@link #DEFAULT_FORMAT} pattern.
     * 
     * @param val The metric value as double
     */
    public MetricValue(double val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    /**
     * Constructs a MetricValue object starting from a double value
     * and initialize the formatter with the <code>prettyPrintFormat</code>
     * pattern.
     * 
     * @param val The metric value
     * @param prettyPrintFormat The format pattern.
     * @see DecimalFormat
     */
    public MetricValue(double val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }

    /**
     * Constructs a MetricValue object starting from a int value
     * and initialize the formatter with the {@link #DEFAULT_FORMAT} pattern.
     * 
     * @param val The metric value as int
     */
    public MetricValue(int val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    /**
     * Constructs a MetricValue object starting from a int value
     * and initialize the formatter with the <code>prettyPrintFormat</code>
     * pattern.
     * 
     * @param val The metric value
     * @param prettyPrintFormat The format pattern
     * @see DecimalFormat
     */
    public MetricValue(int val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }

    /**
     * Constructs a MetricValue object starting from a long value
     * and initialize the formatter with the {@link #DEFAULT_FORMAT} pattern.
     * 
     * @param val The metric value as long
     */
    public MetricValue(long val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    /**
     * Constructs a MetricValue object starting from a long value
     * and initialize the formatter with the <code>prettyPrintFormat</code>
     * pattern.
     * 
     * @param val The metric value
     * @param prettyPrintFormat The format pattern
     * @see DecimalFormat
     */
    public MetricValue(long val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }

    /**
     * Constructs a MetricValue object starting from a string value
     * and initialize the formatter with the {@link #DEFAULT_FORMAT} pattern.
     * 
     * @param val The metric value as string
     */
    public MetricValue(String val) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(DEFAULT_FORMAT);
    }

    /**
     * Constructs a MetricValue object starting from a string value
     * and initialize the formatter with the <code>prettyPrintFormat</code>
     * pattern.
     * 
     * @param val The metric value
     * @param prettyPrintFormat The format pattern
     * @see DecimalFormat
     */
    public MetricValue(String val, String prettyPrintFormat) {
        super(val);
        DECIMAL_FORMAT = new DecimalFormat(prettyPrintFormat);
    }
    
    /**
     * Constructs a MetricValue object starting from a {@link BigDecimal} value
     * and initialize the formatter with the {@link #DEFAULT_FORMAT} pattern.
     * 
     * @param val The metric value as {@link BigDecimal}
     */
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
    
    /**
     * @return this value formatted with the passed in {@link DecimalFormat} 
     * pattern.
     */
    public String toPrettyPrintedString() {
        return DECIMAL_FORMAT.format(this);
    }
}
