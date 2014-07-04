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
 * When the threshold parser is executing this stage it means it is expecting an
 * open or closed bracket.
 *
 * @author Massimiliano Ziccardi
 */
abstract class BracketStage extends Stage {

    /**
     * The bracket to search for (open or closed).
     */
    private final String bracket;

    /**
     * Builds the bracket stage.
     *
     * @param stageName
     *            The name of the stage
     * @param matchedBracket
     *            The bracket to search for
     */
    protected BracketStage(final String stageName, final char matchedBracket) {
        super(stageName);
        bracket = "" + matchedBracket;
    }

    /**
     * Parses the threshold to remove the matched braket.
     *
     * No checks are performed against the passed in string: the object assumes
     * that the string is correct since the {@link #canParse(String)} method
     * <b>must</b> be called <b>before</b> this method.
     *
     * @param threshold
     *            The threshold to parse
     * @param tc
     *            The threshold config object. This object will be populated
     *            according to the passed in threshold.
     * @return the remaining part of the threshold
     */
    @Override
    public String parse(final String threshold, final RangeConfig tc) {
        configure(tc);
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
    @Override
    public boolean canParse(final String threshold) {
        if (threshold == null) {
            return false;
        }

        return threshold.startsWith(bracket);
    }

    /**
     * This method is used to generate the exception message.
     *
     * @return the token that this stage is waiting for.
     */
    @Override
    public String expects() {
        return bracket;
    }

    /**
     * Sets the {@link RangeConfig} object as not left/right inclusive.
     *
     * @param rc
     *            The range configuration
     */
    protected abstract void configure(final RangeConfig rc);

    /**
     * This class implements a 'closed brace stage'. It consumes, if present, a
     * closed brace at the beginning of the received threshold chunk.
     *
     * @author Massimiliano Ziccardi
     */
    public static class ClosedBracketStage extends BracketStage {

        /**
         * 
         */
        private static final long serialVersionUID = -1507593260530752338L;

        /**
         *
         */
        public ClosedBracketStage() {
            super("closedbracket", ')');
        }

        /**
         * Closed bracket can be the end of the range.
         * 
         * @return <code>true</code>
         */
        public final boolean isLeaf() {
            return true;
        }

        /**
         * Sets the {@link RangeConfig} object as not right inclusive.
         *
         * @param rc
         *            The range configuration
         */
        protected final void configure(final RangeConfig rc) {
            rc.setRightInclusive(false);
        }
    }

    /**
     * This class implements a 'open brace stage'. It consumes, if present, an
     * open brace at the beginning of the received threshold chunk.
     *
     * Example Input : (0..100
     *
     * Produced Output : 0..100 and calls the
     * {@link RangeConfig#setLeftInclusive(boolean)} passing <code>true</code>
     *
     * @author Massimiliano Ziccardi
     */
    public static class OpenBracketStage extends BracketStage {

        /**
         * 
         */
        private static final long serialVersionUID = -8909366032913760788L;

        /**
         *
         */
        public OpenBracketStage() {
            super("openbracket", '(');
        }

        /**
         * Sets the {@link RangeConfig} object as not left inclusive.
         *
         * @param rc
         *            The range configuration
         */
        protected final void configure(final RangeConfig rc) {
            rc.setLeftInclusive(false);
        }

    }

}
