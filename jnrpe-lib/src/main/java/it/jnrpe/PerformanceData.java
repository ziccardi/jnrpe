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

import it.jnrpe.ReturnValue.UnitOfMeasure;
import it.jnrpe.plugins.Metric;
import it.jnrpe.utils.thresholds.Prefixes;

/**
 * This class holds the performance data to be returned with the result of the
 * plugin execution.
 *
 * Refer to http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
class PerformanceData {

    /**
     * The Unit of Measure to be used to interpret this performance data.
     */
    private final UnitOfMeasure unitOfMeasure;

    /**
     * The Unit of Measure to be used to interpret this performance data.
     */
    private final String unit;

    /**
     * The warning range passed to the plugin that produced this performance
     * data. Can be <code>null</code>
     */
    private final String warningRange;

    /**
     * The critical range passed to the plugin that produced this performance
     * data. Can be <code>null</code>
     */
    private final String criticalRange;

    private final Prefixes prefix;
    
    private Metric metric = null;
    
    /**
     * Creates a performance data object.
     *
     * @param uom
     *            The Unit of Measure to be used to interpret metric and ranges
     * @param warnRange
     *            the warning range passed to the plugin that is generating this
     *            performance data. Can be null.
     * @param critRange
     *            the critical range passed to the plugin that is generating
     *            this performance data. Can be null.
     */
    PerformanceData(final Metric metric, final UnitOfMeasure uom, final String warnRange, final String critRange) {
        this.metric = metric;
        unitOfMeasure = uom;
        warningRange = warnRange;
        criticalRange = critRange;
        unit = null;
        prefix = Prefixes.RAW;
    }

    /**
     * Creates a performance data object.
     *
     * @param metric
     *            The metric that contains the performance data
     * @param prefix
     *            The prefix to be used to prepare the data to be inserted into
     *            the performance data line (mega, kilo, etc).
     *            It is used to convert the value present inside the metric (for 
     *            example, memory is usually returned in bytes. You could use 
     *            mega here to visualize it as MegaBytes). 
     * @param unitofMeasure
     *            The Unit of Measure of <code>value</code>,
     *            <code>minValue</code> and <code>maxValue</code>
     * @param warnRange
     *            the warning range passed to the plugin that is generating this
     *            performance data. Can be null.
     * @param critRange
     *            the critical range passed to the plugin that is generating
     *            this performance data. Can be null.
     */
    PerformanceData(final Metric metric, final Prefixes prefix, final String unitofMeasure, final String warnRange, final String critRange) {
        this.metric = metric;
        unitOfMeasure = null;
        warningRange = warnRange;
        criticalRange = critRange;
        unit = unitofMeasure;
        this.prefix = prefix;
    }

    /**
     * Produce a performance string according to Nagios specification based on
     * the value of this performance data object.
     *
     * @return a string that can be returned to Nagios 
     */
    public String toPerformanceString() {
        final StringBuilder res = new StringBuilder()
                .append(this.quote(metric.getName()))
                .append('=')
                .append(metric.getOutputMetric())
                .append(metric.getUOM())
                .append(';');

        if (this.metric.getWarningThreshold() != null) {
            res.append(this.metric.getWarningThreshold());
        }
        res.append(';');
        if (this.metric.getCriticalThreshold() != null) {
            res.append(this.metric.getCriticalThreshold());
        }
        res.append(';');
        if (this.metric.getMin() != null) {
            res.append(this.metric.getMin());
        }
        res.append(';');
        if (this.metric.getMax() != null) {
            res.append(this.metric.getMax());
        }

        return res.toString();
    }

    /**
     * Quotes the label if required.
     *
     * @param lbl
     *            The label to be quoted
    
     * @return The quoted label or the original label if quoting is not
     *         required. */
    private String quote(final String lbl) {
        if (lbl.indexOf(' ') == -1) {
            return lbl;
        }

        return new StringBuffer("'").append(lbl).append('\'').toString();
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "PerformanceData [label=" + metric.getName() + ", performanceValue=" + metric.getValue() + ", unitOfMeasure=" + unitOfMeasure + ", unit=" + unit
                + ", warningRange=" + warningRange + ", criticalRange=" + criticalRange + ", minimumValue=" + metric.getMin() + ", maximumValue="
                + metric.getMax() + "]";
    }
}
