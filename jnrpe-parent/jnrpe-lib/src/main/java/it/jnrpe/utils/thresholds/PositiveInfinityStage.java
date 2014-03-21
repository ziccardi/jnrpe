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

/**
 * Parses a negative infinity (+inf). The '+' is optional. See
 * http://nagiosplugins.org/rfc/new_threshold_syntax
 *
 * Example Input : 50..+inf
 *
 * {@link RangeConfig#setPositiveInfinity(boolean)} gets called passing
 * <code>true</code>
 *
 * Positive infinity can only be at the end of a range.
 *
 * @author Massimiliano Ziccardi
 */
class PositiveInfinityStage extends Stage {

    /**
     * The infinity sign.
     */
    private static final String INFINITY = "inf";

    /**
     * The negative infinity sign.
     */
    private static final String POS_INFINITY = "+inf";

    /**
     *
     */
    protected PositiveInfinityStage() {
        super("positiveinfinity");
    }

    /**
     * Parses the threshold to remove the matched 'inf' or '+inf' string.
     *
     * No checks are performed against the passed in string: the object
     * assumes that the string is correct since the {@link #canParse(String)}
     * method <b>must</b> be called <b>before</b> this method.
     *
     * @param threshold
     *            The threshold chunk to be parsed
     * @param tc
     *            The threshold config object. This object will be populated
     *            according to the passed in threshold.
     * @return the remaining part of the threshold
     */
    @Override
    public String parse(final String threshold, final RangeConfig tc) {

        tc.setPositiveInfinity(true);

        if (threshold.startsWith(INFINITY)) {
            return threshold.substring(INFINITY.length());
        } else {
            return threshold.substring(POS_INFINITY.length());
        }
    }

    @Override
    public boolean canParse(final String threshold) {
        if (threshold == null) {
            return false;
        }
        return threshold.startsWith("inf") || threshold.startsWith("+inf");
    }

    @Override
    public String expects() {
        return "[+]inf";
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
