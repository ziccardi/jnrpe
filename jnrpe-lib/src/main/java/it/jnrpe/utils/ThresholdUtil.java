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

import it.jnrpe.utils.thresholds.LegacyRange;

import java.math.BigDecimal;

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
 * together with the
 * {@link it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder} instead.
 */
@Deprecated
public final class ThresholdUtil {
    /**
     * Private default constructor to avoid instantiation.
     */
    private ThresholdUtil() {

    }

    /**
     * Returns <code>true</code> if the value <code>iValue</code> falls into the
     * range <code>sRange</code>.
     *
     * @param thresholdString
     *            The range
     * @param value
     *            The value
     * @return <code>true</code> if the value <code>iValue</code> falls into the
     * @throws BadThresholdException
     *             -
     */
    public static boolean isValueInRange(final String thresholdString,
            final int value) throws BadThresholdException {
        return new LegacyRange(thresholdString).isValueInside(value);
    }

    /**
     * Returns <code>true</code> if the value <code>dalue</code> falls into the
     * range <code>sRange</code>.
     *
     * @param thresholdString
     *            The range
     * @param value
     *            The value given range
     * @return <code>true</code> if the given value falls inside the given range
     * @throws BadThresholdException
     *             -
     */
    public static boolean isValueInRange(final String thresholdString,
            final BigDecimal value) throws BadThresholdException {
        return new LegacyRange(thresholdString).isValueInside(value);
    }

    /**
     * Returns <code>true</code> if the value <code>dalue</code> falls into the
     * range <code>sRange</code>.
     *
     * @param thresholdString
     *            The range
     * @param value
     *            The value given range
     * @return <code>true</code> if the given value falls inside the given range
     * @throws BadThresholdException
     *             -
     */
    public static boolean isValueInRange(final String thresholdString,
            final Long value) throws BadThresholdException {
        return new LegacyRange(thresholdString).isValueInside(new BigDecimal(
                value));
    }
}
