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
package it.jnrpe.utils;

import it.jnrpe.plugins.Metric;
import it.jnrpe.utils.thresholds.LegacyRange;
import it.jnrpe.utils.thresholds.Prefixes;

/**
 * Utility class for evaluating threshold This class conforms to the nagios
 * plugin guidelines
 * (http://nagiosplug.sourceforge.net/developer-guidelines.html
 * #THRESHOLDFORMAT). The generalised range format is: [@]start:end Values are
 * interpreted this way:
 * <ul>
 * <li>if range is of format "start:" and end is not specified, assume end is
 * infinity
 * <li>to specify negative infinity, use "~"
 * <li>alert is raised if metric is outside start and end range (inclusive of
 * endpoints)
 * <li>if range starts with "@", then alert if inside this range (inclusive of
 * endpoints)
 * </ul>
 * start and ":" is not required if start=0.
 *
 * @author Massimiliano Ziccardi
 * @deprecated Use the {@link it.jnrpe.utils.thresholds.ReturnValueBuilder}
 *             together with the
 *             {@link it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder}
 *             instead.
 * @version $Revision: 1.0 $
 */
@Deprecated
public final class ThresholdUtil {
    
    /**
     * Private default constructor to avoid instantiation.
     */
    private ThresholdUtil() {

    }

    /**
     * Returns <code>true</code> if the metric value <code>metric</code> 
     * falls into the passed in <code>range</code>.
     *
     * @param range
     *            The range
     * @param metric
     *            The metric to be checked
     *            
     * @return <code>true</code> if the metric value <code>metric</code> 
     *          falls into the passed in <code>range</code>.
     * @throws BadThresholdException
     */
    public static boolean isValueInRange(final String range, final Metric metric) throws BadThresholdException {
        return new LegacyRange(range).isValueInside(metric);
    }
    
    /**
     * Returns <code>true</code> if the metric value <code>metric</code> 
     * falls into the passed in <code>range</code>.
     * The metric value is transformed according to the passed in prefix.
     * For example, if the metric is in {@link Prefixes#kilo} and the passed in prefix is
     * {@link Prefixes#mega} then the value is converted from kilo to mega before being 
     * evaluated.
     *
     * @param range
     *            The range
     * @param metric
     *            The metric to be checked
     *            
     * @return <code>true</code> if the metric value <code>metric</code> 
     *          falls into the passed in <code>range</code>.
     * @throws BadThresholdException
     */
    public static boolean isValueInRange(final String range, final Metric metric, final Prefixes prefix) throws BadThresholdException {
        return new LegacyRange(range, prefix).isValueInside(metric);
    }
}
