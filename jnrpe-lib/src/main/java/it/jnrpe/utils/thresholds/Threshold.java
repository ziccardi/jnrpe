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
import it.jnrpe.utils.BadThresholdException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * The threshold interface. This object must be used to verify if a value falls
 * inside one of the ok, warning or critical ranges.
 *
 * According to nagios specifications, the evaluation order is the following:
 *
 * <ul>
 * <li>If no levels are specified, return OK
 * <li>If an ok level is specified and value is within range, return OK
 * <li>If a critical level is specified and value is within range, return
 * CRITICAL
 * <li>If a warning level is specified and value is within range, return WARNING
 * <li>If an ok level is specified, return CRITICAL
 * <li>Otherwise return OK
 * </ul>
 *
 * @author Massimiliano Ziccardi
 *
 */
class Threshold implements IThreshold {

    /**
     * The name of the metric attached to this threshold.
     */
    private String metricName = null;

    /**
     * The list of ok ranges.
     */
    private List<Range> okThresholdList = new ArrayList<Range>();
    /**
     * The list of warning ranges.
     */
    private List<Range> warningThresholdList = new ArrayList<Range>();
    /**
     * The list of critical ranges.
     */
    private List<Range> criticalThresholdList = new ArrayList<Range>();

    /**
     * The unit of measures. It is a free string and should be used only for
     * checks where the check plugin do not know the unit of measure (for
     * example, JMX).
     */
    private String unit = null;

    /**
     * The prefix to be used to multiply the range values and divide the metric
     * values.
     */
    private Prefixes prefix = null;

    /**
     * Build a threshold object parsing the string received. A threshold can be
     * in the format: <blockquote>
     * metric={metric},ok={range},warn={range},crit={
     * range},unit={unit},prefix={SI prefix} </blockquote>
     *
     * Where :
     * <ul>
     * <li>ok, warn, crit are called "levels"
     * <li>any of ok, warn, crit, unit or prefix are optional
     * <li>if ok, warning and critical are not specified, then ok is always
     * returned
     * <li>the unit can be specified with plugins that do not know about the
     * type of value returned (SNMP, Windows performance counters, etc.)
     * <li>the prefix is used to multiply the input range and possibly for
     * display data. The prefixes allowed are defined by NIST:
     * <ul>
     * <li>http://physics.nist.gov/cuu/Units/prefixes.html
     * <li>http://physics.nist.gov/cuu/Units/binary.html
     * </ul>
     * <li>ok, warning or critical can be repeated to define an additional
     * range. This allows non-continuous ranges to be defined
     * <li>warning can be abbreviated to warn or w
     * <li>critical can be abbreviated to crit or c
     * </ul>
     *
     * @param definition
     *            The threshold string
     * @throws BadThresholdException
     *             -
     */
    Threshold(final String definition) throws BadThresholdException {
        parse(definition);
    }

    /**
     * Parses a threshold definition.
     *
     * @param definition
     *            The threshold definition
     * @throws BadThresholdException
     *             -
     */
    private void parse(final String definition) throws BadThresholdException {
        String[] thresholdComponentAry = definition.split(",");

        for (String thresholdComponent : thresholdComponentAry) {
            String[] nameValuePair = thresholdComponent.split("=");

            if (nameValuePair.length != 2 || StringUtils.isEmpty(nameValuePair[0]) || StringUtils.isEmpty(nameValuePair[1])) {
                throw new BadThresholdException("Invalid threshold syntax : " + definition);
            }

            if (nameValuePair[0].equalsIgnoreCase("metric")) {
                metricName = nameValuePair[1];
                continue;
            }
            if (nameValuePair[0].equalsIgnoreCase("ok")) {

                Range thr = new Range(nameValuePair[1]);

                okThresholdList.add(thr);
                continue;
            }
            if (nameValuePair[0].equalsIgnoreCase("warning") || nameValuePair[0].equalsIgnoreCase("warn") || nameValuePair[0].equalsIgnoreCase("w")) {
                Range thr = new Range(nameValuePair[1]);
                warningThresholdList.add(thr);
                continue;
            }
            if (nameValuePair[0].equalsIgnoreCase("critical") || nameValuePair[0].equalsIgnoreCase("crit") || nameValuePair[0].equalsIgnoreCase("c")) {
                Range thr = new Range(nameValuePair[1]);
                criticalThresholdList.add(thr);
                continue;
            }
            if (nameValuePair[0].equalsIgnoreCase("unit")) {
                unit = nameValuePair[1];
                continue;
            }
            if (nameValuePair[0].equalsIgnoreCase("prefix")) {
                prefix = Prefixes.fromString(nameValuePair[1].toLowerCase());
                continue;
            }

            // Threshold specification error
        }
    }

    /**
     * @return The name of the metric associated to this threshold.
     */
    public final String getMetric() {
        return metricName;
    }

    /**
     * @return The unit of measure attached to the appropriate prefix if
     *         specified.
     */
    public final String getUnitString() {
        StringBuilder res = new StringBuilder();
        if (prefix != null) {
            res.append(prefix);
        }
        if (unit != null) {
            res.append(unit);
        }

        if (res.length() != 0) {
            return res.toString();
        }

        return null;
    }

    /**
     * Returns the requested range list as comma separated string.
     *
     * @param status
     *            The status for wich we are requesting the ranges.
     * @return the requested range list as comma separated string.
     */
    public final String getRangesAsString(final Status status) {
        List<String> ranges = new ArrayList<String>();

        List<Range> rangeList = null;

        switch (status) {
        case OK:
            rangeList = okThresholdList;
            break;
        case WARNING:
            rangeList = warningThresholdList;
            break;
        case CRITICAL:
        default:
            rangeList = criticalThresholdList;
            break;
        }

        for (Range r : rangeList) {
            ranges.add(r.getRangeString());
        }

        if (ranges.isEmpty()) {
            return null;
        }

        return StringUtils.join(ranges, ",");
    }

    /**
     * Evaluates this threshold against the passed in value. The returned status
     * is computed this way:
     * <ol>
     * <li>If at least one ok range is specified, if the value falls inside one
     * of the ok ranges, {@link Status#OK} is returned.
     * <li>If at lease one critical range is specified, if the value falls
     * inside one of the critical ranges, {@link Status#CRITICAL} is returned.
     * <li>If at lease one warning range is specified, if the value falls inside
     * one of the warning ranges, {@link Status#WARNING} is returned.
     * <li>If neither of the previous match, but at least an OK range has been
     * specified, return {@link Status#CRITICAL}.
     * <li>Otherwise return {@link Status#OK}
     * </ol>
     * 
     * @param value
     *            The value to be evaluated.
     * @return The computes status.
     */
    public final Status evaluate(final BigDecimal value) {
        if (okThresholdList.isEmpty() && warningThresholdList.isEmpty() && criticalThresholdList.isEmpty()) {
            return Status.OK;
        }

        BigDecimal convertedValue = value;

        // Perform evaluation escalation
        for (Range range : okThresholdList) {
            if (range.isValueInside(convertedValue, prefix)) {
                return Status.OK;
            }
        }

        for (Range range : criticalThresholdList) {
            if (range.isValueInside(convertedValue, prefix)) {
                return Status.CRITICAL;
            }
        }

        for (Range range : warningThresholdList) {
            if (range.isValueInside(convertedValue, prefix)) {
                return Status.WARNING;
            }
        }

        if (!okThresholdList.isEmpty()) {
            return Status.CRITICAL;
        }

        return Status.OK;
    }

    /**
     * @param metric
     *            The name of the metric we want to evaluate.
     * @return <code>true</code> if this threshold is about the passed in
     *         metric.
     */
    public final boolean isAboutMetric(final String metric) {
        return metric.equalsIgnoreCase(metricName);
    }
}
