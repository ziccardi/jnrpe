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
 * This stage handles the negate character ('^').
 *
 * Example Input : ^(0..100
 *
 * Produced Output : (0..100 and calls the
 * {@link RangeConfig#setNegate(boolean)} passing <code>true</code>
 *
 * @author Massimiliano Ziccardi
 */
class NegateStage extends Stage {

    /**
     *
     */
    protected NegateStage() {
        super("negate");
    }

    /**
     * Parses the threshold to remove the matched '^' char.
     *
     * No checks are performed against the passed in string: the object assumes
     * that the string is correct since the {@link #canParse(String)} method
     * <b>must</b> be called <b>before</b> this method.
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
        tc.setNegate(true);
        return threshold.substring(1);
    }

    /**
     * Tells the parser if this stage is able to parse the current remaining
     * threshold part.
     *
     * @param threshold
     *            The threshold part to be parsed.
     * @return <code>true</code> if this object can consume a part of the
     *         threshold
     */
    public boolean canParse(final String threshold) {
        if (threshold == null) {
            return false;
        }
        return threshold.startsWith("^");
    }

    /**
     * This method is used to generate the exception message.
     *
     * @return the token that this stage is waiting for.
     */
    @Override
    public String expects() {
        return "^";
    }
}
