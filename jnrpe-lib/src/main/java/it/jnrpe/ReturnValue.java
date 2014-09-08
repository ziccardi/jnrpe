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
package it.jnrpe;

import it.jnrpe.utils.thresholds.ReturnValueBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is just a container for the plugin result.
 * 
 * @author Massimiliano Ziccardi
 */
public final class ReturnValue {
    /**
     * An enumeration defining all supported unit of measures.
     * 
     * @author Massimiliano Ziccardi
     */
    public enum UnitOfMeasure {
        /**
         * microseconds.
         */
        microseconds,
        /**
         * milliseconds.
         */
        milliseconds,
        /**
         * seconds.
         */
        seconds,
        /**
         * percentage.
         */
        percentage,
        /**
         * bytes.
         */
        bytes,
        /**
         * kilobytes.
         */
        kilobytes,
        /**
         * megabytes.
         */
        megabytes,
        /**
         * gigabytes.
         */
        gigabytes,
        /**
         * terabytes.
         */
        terabytes,
        /**
         * counter.
         */
        counter
    };

    /**
     * The performance data to attach to the result string.
     */
    private final List<PerformanceData> performanceDataList = new ArrayList<PerformanceData>();

    /**
     * The raw return code.
     */
    private Status statusCode;

    /**
     * The message.
     */
    private String messageString;

    /**
     * Initializes an empty return value.
     */
    public ReturnValue() {

    }

    /**
     * Initializes the return value object with the given message and with the
     * {@link Status.OK} state.
     * 
     * @param message
     *            The message
     */
    public ReturnValue(final String message) {
        statusCode = Status.OK;
        messageString = message;
    }

    /**
     * Initializes the return value object with the given state and the given
     * message.
     * 
     * @param returnCode
     *            The state
     * @param message
     *            The message
     * @deprecated Use {@link #ReturnValue(Status, String)} instead
     */
    @Deprecated
    public ReturnValue(final int returnCode, final String message) {
        statusCode = Status.fromIntValue(returnCode);
        messageString = message;
    }

    /**
     * Initializes the return value object with the given state and the given
     * message.
     * 
     * @param status
     *            The status to be returned
     * @param message
     *            The message to be returned
     */
    public ReturnValue(final Status status, final String message) {
        statusCode = status;
        messageString = message;
    }

    /**
     * Sets the return code and returns 'this' so that the calls can be
     * cascaded.
     * 
     * @param returnCode
     *            The return code
     * @return this
     * @deprecated Use {@link #withStatus(Status)} instead.
     */
    @Deprecated
    public ReturnValue withReturnCode(final int returnCode) {
        statusCode = Status.fromIntValue(returnCode);
        return this;
    }

    /**
     * Sets the return code and returns 'this' so that the calls can be
     * cascaded.
     * 
     * @param status
     *            The status to be returned to Nagios
     * @return this
     */
    public ReturnValue withStatus(final Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status can't be null");
        }

        statusCode = status;
        return this;
    }

    /**
     * Sets the message and returns 'this' so that the calls can be cascaded.
     * 
     * @param message
     *            The message to be returned
     * @return this
     */
    public ReturnValue withMessage(final String message) {
        messageString = message;
        return this;
    }

    /**
     * Returns the status.
     * 
     * @return The state
     * @deprecated Use {@link #getStatus()} instead.
     */
    @Deprecated
    public int getReturnCode() {
        return statusCode.intValue();
    }

    /**
     * Returns the status.
     * 
     * @return The status
     */
    public Status getStatus() {
        return statusCode;
    }

    /**
     * Returns the message. If the performance data has been passed in, they are
     * attached at the end of the message accordingly to the Nagios
     * specifications
     * 
     * @return The message and optionally the performance data
     */
    public String getMessage() {
        if (performanceDataList.isEmpty()) {
            return messageString;
        }
        StringBuilder res = new StringBuilder(messageString).append('|');
        for (PerformanceData pd : performanceDataList) {
            res.append(pd.toPerformanceString()).append(' ');
        }
        return res.toString();
    }

    /**
     * Adds performance data to the plugin result. Thos data will be added to
     * the output formatted as specified in Nagios specifications
     * (http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201)
     * 
     * @param label
     *            The label of the performance data we are adding
     * @param value
     *            The performance data value
     * @param uom
     *            The Unit Of Measure
     * @param warningRange
     *            The warning threshold used to check this metric (can be null)
     * @param criticalRange
     *            The critical threshold used to check this value (can be null)
     * @param minimumValue
     *            The minimum value for this metric (can be null if not
     *            applicable)
     * @param maximumValue
     *            The maximum value for this metric (can be null if not
     *            applicable)
     * @return this
     */
    public ReturnValue withPerformanceData(final String label, final Long value, final UnitOfMeasure uom, final String warningRange,
            final String criticalRange, final Long minimumValue, final Long maximumValue) {
        BigDecimal bdValue = null;
        BigDecimal bdMin = null;
        BigDecimal bdMax = null;

        if (value != null) {
            bdValue = new BigDecimal(value);
        }
        if (minimumValue != null) {
            bdMin = new BigDecimal(minimumValue);
        }
        if (maximumValue != null) {
            bdMax = new BigDecimal(maximumValue);
        }

        performanceDataList.add(new PerformanceData(label, bdValue, uom, warningRange, criticalRange, bdMin, bdMax));
        return this;
    }

    /**
     * Adds performance data to the plugin result. Thos data will be added to
     * the output formatted as specified in Nagios specifications
     * (http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201)
     * 
     * @param label
     *            The label of the performance data we are adding
     * @param value
     *            The performance data value
     * @param uom
     *            The Unit Of Measure
     * @param warningRange
     *            The warning threshold used to check this metric (can be null)
     * @param criticalRange
     *            The critical threshold used to check this value (can be null)
     * @param minimumValue
     *            The minimum value for this metric (can be null if not
     *            applicable)
     * @param maximumValue
     *            The maximum value for this metric (can be null if not
     *            applicable)
     * @return this
     */
    public ReturnValue withPerformanceData(final String label, final BigDecimal value, final UnitOfMeasure uom, final String warningRange,
            final String criticalRange, final BigDecimal minimumValue, final BigDecimal maximumValue) {
        performanceDataList.add(new PerformanceData(label, value, uom, warningRange, criticalRange, minimumValue, maximumValue));
        return this;
    }

    /**
     * Adds performance data to the plugin result. Thos data will be added to
     * the output formatted as specified in Nagios specifications
     * (http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201)
     * 
     * @param label
     *            The label of the performance data we are adding
     * @param value
     *            The performance data value
     * @param unit
     *            The Unit Of Measure
     * @param warningRange
     *            The warning threshold used to check this metric (can be null)
     * @param criticalRange
     *            The critical threshold used to check this value (can be null)
     * @param minimumValue
     *            The minimum value for this metric (can be null if not
     *            applicable)
     * @param maximumValue
     *            The maximum value for this metric (can be null if not
     *            applicable)
     * @return this
     */
    public ReturnValue withPerformanceData(final String label, final BigDecimal value, final String unit, final String warningRange,
            final String criticalRange, final BigDecimal minimumValue, final BigDecimal maximumValue) {

        performanceDataList.add(new PerformanceData(label, value, unit, warningRange, criticalRange, minimumValue, maximumValue));
        return this;
    }
    
    public static void main(String[] args) {
        ReturnValue rv = new ReturnValue("message")
            .withStatus(Status.OK)
            .withPerformanceData("MyLabel", new BigDecimal("10"), (UnitOfMeasure) null, "10:", "20:", new BigDecimal(0), new BigDecimal(10));
        
        System.out.println (rv.getMessage());
    }
}
