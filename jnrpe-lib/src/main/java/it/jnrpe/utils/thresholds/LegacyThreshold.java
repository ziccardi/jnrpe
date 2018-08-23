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

import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;

/**
 * This class represent a parser/evaluator for the old threshold syntax.
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class LegacyThreshold implements IThreshold {
    /**
     * The range for an OK status.
     */
    private final LegacyRange okRange;
    /**
     * The range for a Warning status.
     */
    private final LegacyRange warnRange;
    /**
     * The range for a critical status.
     */
    private final LegacyRange critRange;
    /**
     * The metric associated with this threshold specification.
     */
    private final String metricName;

    /**
     * The prefix to be used to interpret the boundaries of this
     * range specification.
     */
    private final Prefixes prefix;
    
    /**
     * Builds and initializes the threshold object.
     *
     * @param prefix
     *            The prefix to be used to interpret the boundaries of this
     *            range specification
     * @param metric
     *            The metric associated with this threshold.
     * @param ok
     *            The ok range.
     * @param warn
     *            The warning range.
     * @param crit
     *            The critical range.
     */
    public LegacyThreshold(final Prefixes prefix, final String metric, final LegacyRange ok, final LegacyRange warn, final LegacyRange crit) {
        okRange = ok;
        warnRange = warn;
        critRange = crit;
        metricName = metric;
        this.prefix = prefix;
    }

    /**
     * Builds and initializes the threshold object.
     *
     * @param metric
     *            The metric associated with this threshold.
     * @param ok
     *            The ok range.
     * @param warn
     *            The warning range.
     * @param crit
     *            The critical range.
     */
    public LegacyThreshold(final String metric, final LegacyRange ok, final LegacyRange warn, final LegacyRange crit) {
        okRange = ok;
        warnRange = warn;
        critRange = crit;
        metricName = metric;
        this.prefix = Prefixes.RAW;
    }
    
    /**
     * Evaluates the metric value against the {@link #okRange}, 
     * {@link #warnRange} and {@link #critRange}. 
     * 
     * The followed flow is:
     * <ol>
     * <li>If a critical range is defined and the value falls inside the
     * specified range, a {@link Status#CRITICAL} is returned.
     * <li>If a warning range is defined and the value falls inside the
     * specified range, a {@link Status#WARNING} is returned.
     * <li>If a OK range is defined and the value falls inside the specified
     * range, a {@link Status#OK} is returned.
     * <li>If the OK range is not specified, a {@link Status#CRITICAL} is
     * returned
     * <li>{@link Status#OK} is returned
     * </ol>
     * 
     * @param value
     *            The value to be evaluated.
     *            
     * @return the evaluated status. 
     * 
     * @see it.jnrpe.utils.thresholds.IThreshold#evaluate(Metric)
     */
    public final Status evaluate(final Metric value) {
        if (critRange != null && critRange.isValueInside(value)) {
            return Status.CRITICAL;
        }
        if (warnRange != null && warnRange.isValueInside(value)) {
            return Status.WARNING;
        }
        if (okRange != null) {
            if (okRange.isValueInside(value)) {
                return Status.OK;
            } else {
                return Status.CRITICAL;
            }
        }

        return Status.OK;
    }

    /**
     * @param metric
     *            The metric we want to evaluate.
     *            
     * @return whether this threshold is about the passed in metric. 
     * @see it.jnrpe.utils.thresholds.IThreshold#isAboutMetric(Metric)
     */
    public final boolean isAboutMetric(final Metric metric) {
        return metricName.equalsIgnoreCase(metric.getMetricName());
    }

    /**
     * The metric referred by this threshold.
     * 
     * @return the metric name. 
     * @see it.jnrpe.utils.thresholds.IThreshold#getMetric()
     */
    public final String getMetric() {
        return metricName;
    }

    /**
     * @param status
     *            the range we are interested in.
     * @return the requested unparsed range string. 
     * @see it.jnrpe.utils.thresholds.IThreshold#getRangesAsString(Status)
     */
    public final String getRangesAsString(final Status status) {
        switch (status) {
        case OK:
            if (okRange != null) {
                return okRange.getThresholdString();
            }
            break;
        case WARNING:
            if (warnRange != null) {
                return warnRange.getThresholdString();
            }
            break;
        case CRITICAL:
            if (critRange != null) {
                return critRange.getThresholdString();
            }
            break;
        default:
        }

        return null;
    }

    /**
     * @return the unit of measure as string. 
     * @see it.jnrpe.utils.thresholds.IThreshold#getUnitString()
     */
    public final String getUnitString() {
        return null;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "LegacyThreshold [okRange=" + okRange + ", warnRange=" + warnRange + ", critRange=" + critRange + ", metricName=" + metricName + "]";
    }
    
    /**
     * Returns the prefix configured for this threshold.
     * @see #prefix
     */
    public Prefixes getPrefix() {
        return prefix;
    }
}
