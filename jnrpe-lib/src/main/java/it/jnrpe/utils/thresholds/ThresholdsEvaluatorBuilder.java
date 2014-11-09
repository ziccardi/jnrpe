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

import it.jnrpe.utils.BadThresholdException;

/**
 * Builds a {@link ThresholdsEvaluator} object.
 *
 * @author Massimiliano Ziccardi
 *
 * @version $Revision: 1.0 $
 */
public class ThresholdsEvaluatorBuilder {

    /**
     * The threshold evaluator instance.
     */
    private ThresholdsEvaluator thresholds = new ThresholdsEvaluator();

    /**
     * Default constructor.
     */
    public ThresholdsEvaluatorBuilder() {
    }

    /**
     * Adds a threshold to the threshold evaluator object.
     *
     * @param threshold
     *            The threhsold text
    
    
     * @return this * @throws BadThresholdException
     *             - */
    public final ThresholdsEvaluatorBuilder withThreshold(final String threshold) throws BadThresholdException {
        thresholds.addThreshold(new Threshold(threshold));
        return this;
    }

    /**
     * This method allows to specify thresholds using the old format.
     *
     * @param metric
     *            The metric for which this threshold must be configured
     * @param okRange
     *            The ok range (can be null)
     * @param warnRange
     *            The warning range (can be null)
     * @param critRange
     *            The critical range (can be null).
    
    
     * @return this * @throws BadThresholdException
     *             If the threshold can't be parsed. */
    public final ThresholdsEvaluatorBuilder withLegacyThreshold(final String metric, final String okRange, final String warnRange,
            final String critRange) throws BadThresholdException {
        LegacyRange ok = null, warn = null, crit = null;

        if (okRange != null) {
            ok = new LegacyRange(okRange);
        }
        if (warnRange != null) {
            warn = new LegacyRange(warnRange);
        }
        if (critRange != null) {
            crit = new LegacyRange(critRange);
        }

        thresholds.addThreshold(new LegacyThreshold(metric, ok, warn, crit));
        return this;
    }

    /**
    
     * @return the threshold evaluator. */
    public final ThresholdsEvaluator create() {
        return thresholds;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "ThresholdsEvaluatorBuilder [thresholds=" + thresholds + "]";
    }
}
