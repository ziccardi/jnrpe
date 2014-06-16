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

import java.math.BigDecimal;

/**
 * Builds the range object parsing the range passed inside the threshold
 * definition.
 * 
 * A range can have the following format:
 * <p>
 * 
 * [^](start..end)
 * <p>
 * 
 * Where:
 * <ul>
 * <li>start and end must be defined
 * <li>start and end match the regular expression /^[+-]?[0-9]+\.?[0-9]*$|^inf$/
 * (ie, a numeric or "inf")
 * <li>start &lt= end
 * <li>if start = "inf", this is negative infinity. This can also be written as
 * "-inf"
 * <li>if end = "inf", this is positive infinity
 * <li>endpoints are excluded from the range if () are used, otherwise endpoints
 * are included in the range
 * <li>alert is raised if value is within start and end range, unless ^ is used,
 * in which case alert is raised if outside the range
 * </ul>
 * 
 * @author Massimiliano Ziccardi
 */
class Range extends RangeConfig {

	/**
	 * The unparsed range string.
	 */
	private final String rangeString;

	/**
	 * 
	 * @param range
	 *            The range to be parsed
	 * @throws RangeException
	 *             -
	 */
	public Range(final String range) throws RangeException {
		rangeString = range;
		parse(range);
	}

	/**
	 * 
	 * @param range
	 *            The range to be parsed
	 * @throws RangeException
	 *             -
	 */
	private void parse(final String range) throws RangeException {
		RangeStringParser.parse(range, this);
	}

	/**
	 * Multiply the value with the right multiplier based on the prefix.
	 * 
	 * @param value
	 *            The value
	 * @param prefix
	 *            The prefix
	 * @return The result
	 */
	private BigDecimal convert(final BigDecimal value, final Prefixes prefix) {
		if (prefix == null) {
			return value;
		}

		return prefix.convert(value);
	}

	/**
	 * Evaluates if the passed in value falls inside the range. The negation is
	 * ignored.
	 * 
	 * @param value
	 *            The value to evaluate
	 * @param prefix
	 *            Used to multiply the range boundaries.
	 * @return <code>true</code> if the value falls inside the range. The
	 *         negation ('^') is ignored.
	 */
	private boolean evaluate(final BigDecimal value, final Prefixes prefix) {
		if (value == null) {
			throw new NullPointerException("Passed in value can't be null");
		}

		if (!isNegativeInfinity()) {
			switch (value.compareTo(convert(getLeftBoundary(), prefix))) {
			case 0:
				if (!isLeftInclusive()) {
					return false;
				}
				break;
			case -1:
				return false;
			default:
			}
		}

		if (!isPositiveInfinity()) {
			switch (value.compareTo(convert(getRightBoundary(), prefix))) {
			case 0:
				if (!isRightInclusive()) {
					return false;
				}
				break;
			case 1:
				return false;
			default:
			}
		}

		return true;
	}

	/**
	 * @param value
	 *            The value to be evaluated.
	 * @return Whether the passed in value falls inside this range.
	 */
	public boolean isValueInside(final BigDecimal value) {
		return isValueInside(value, null);
	}

	/**
	 * @param value
	 *            The value to be evaluated.
	 * @param prefix
	 *            Used to multiply the range boundaries.
	 * @return Whether the passed in value falls inside this range.
	 */
	public boolean isValueInside(final BigDecimal value, final Prefixes prefix) {
		boolean res = evaluate(value, prefix);

		if (isNegate()) {
			return !res;
		} else {
			return res;
		}
	}

	/**
	 * @param value
	 *            The value to be evaluated.
	 * @return Whether the passed in value falls inside this range.
	 */
	public boolean isValueInside(final int value) {
		return isValueInside(new BigDecimal(value));
	}

	/**
	 * @param value
	 *            The value to be evaluated.
	 * @return Whether the passed in value falls inside this range.
	 */
	public boolean isValueInside(final long value) {
		return isValueInside(new BigDecimal(value));
	}

	/**
	 * @return The unparsed range string.
	 */
	String getRangeString() {
		return rangeString;
	}
}
