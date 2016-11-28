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

import it.jnrpe.plugins.Metric;
import it.jnrpe.utils.thresholds.IThreshold;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is just a container for the plugin result.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
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
     * {@link Status#OK} state.
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
     * @deprecated Use {@link #withStatus(Status)} instead.
     * @return this */
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
     * @return this */
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
     * @return this */
    public ReturnValue withMessage(final String message) {
        messageString = message;
        return this;
    }

    /**
     * Returns the status.
     * 
    
     * @deprecated Use {@link #getStatus()} instead.
     * @return The state */
    @Deprecated
    public int getReturnCode() {
        return statusCode.intValue();
    }

    /**
     * Returns the status.
     * 
    
     * @return The status */
    public Status getStatus() {
        return statusCode;
    }

    /**
     * Returns the message. If the performance data has been passed in, they are
     * attached at the end of the message accordingly to the Nagios
     * specifications
     * 
    
     * @return The message and optionally the performance data */
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
     * @param metric
     *            The metric relative to this result
     * @param uom
     *            The Unit Of Measure
     * @param warningRange
     *            The warning threshold used to check this metric (can be null)
     * @param criticalRange
     *            The critical threshold used to check this value (can be null)
     * @return this
     */
    public ReturnValue withPerformanceData(final Metric metric, final UnitOfMeasure uom, final String warningRange,
            final String criticalRange) {
        performanceDataList.add(new PerformanceData(metric, uom, warningRange, criticalRange));
        return this;
    }

    /**
     * Adds performance data to the plugin result. Thos data will be added to
     * the output formatted as specified in Nagios specifications
     * (http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201)
     * 
     * @param metric
     *            The metric relative to this result
     * @param threshold
     *            The treshold to be evaluated
     * @return this
     */
    public ReturnValue withPerformanceData(final Metric metric, IThreshold threshold) {

        performanceDataList.add(new PerformanceData(metric, threshold.getPrefix(), threshold.getUnitString(), threshold.getRangesAsString(Status.WARNING), threshold.getRangesAsString(Status.CRITICAL)));
        return this;
    }
    
    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        final int maxLen = 10;
        return "ReturnValue [performanceDataList="
                + (performanceDataList != null ? performanceDataList.subList(0, Math.min(performanceDataList.size(), maxLen)) : null)
                + ", statusCode=" + statusCode + ", messageString=" + messageString + "]";
    }
}
