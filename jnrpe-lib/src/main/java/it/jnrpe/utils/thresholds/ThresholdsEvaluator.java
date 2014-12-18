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

import java.util.HashMap;
import java.util.Map;

/**
 * This object take responsability of checking a pair value-metric agains all
 * the ranges defined for such metric and returns the right {@link Status}
 * object.
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class ThresholdsEvaluator {

    /**
     * Default constructor.
     */
    ThresholdsEvaluator() {

    }

    /**
     * This map contains all the pair METRIC-THRESHOLD.
     */
    private Map<String, IThreshold> thresholdsMap = new HashMap<String, IThreshold>();

    /**
     * Adds a threshold to the list of handled thresholds.
     *
     * @param thr
     *            The threshold to add
     */
    final void addThreshold(final IThreshold thr) {
        thresholdsMap.put(thr.getMetric(), thr);
    }

    /**
     * Returns <code>true</code> if the requested metric is required.
     *
     * @param metricName
     *            The name of the metric
     *            
     * @return <code>true</code> if the metric is required. 
     */
    final boolean isMetricRequired(final String metricName) {
        return thresholdsMap.containsKey(metricName);
    }

    /**
     * Returns the requested threshold.
     *
     * @param metric
     *            The metric name attached to the threshold.
     * @return The threshold 
     */
    final IThreshold getThreshold(final String metric) {
        return thresholdsMap.get(metric);
    }

    /**
     * Evaluates the passed in metric against the threshold configured inside
     * this evaluator. If the threshold do not refer the passed in metric, then
     * it is ignored and the next threshold is checked.
     *
     * @param metric
     *            The metric to be checked
    
     * @return The status computed according to the rules specified for
     *         {@link IThreshold#evaluate(Metric)} 
     */
    final Status evaluate(final Metric metric) {

        if (metric == null || metric.getMetricValue() == null) {
            throw new NullPointerException("Metric value can't be null");
        }

        IThreshold thr = thresholdsMap.get(metric.getMetricName());
        if (thr == null) {
            return Status.OK;
        }

        return thr.evaluate(metric);
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "ThresholdsEvaluator [thresholdsMap=" + thresholdsMap + "]";
    }
}
