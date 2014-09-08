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
 * The threshold configuration. It is an interpretation of the threshold string.
 * This object is populated by the threshold parser.
 *
 * @author Massimiliano Ziccardi
 *
 */
class RangeConfig {
    /**
     * Indicate if the threshold is negated.
     */
    private boolean negate = false;

    /**
     * Indicate if the left boundary is inclusive.
     */
    private boolean leftInclusive = true;

    /**
     * Indicate if the right boundary is inclusive.
     */
    private boolean rightInclusive = true;

    /**
     * The left boundary. Must be <code>null</code> if {@link #negativeInfinity}
     * is <code>true</code>
     */
    private BigDecimal startBoundary = null;

    /**
     * The right boundary. Must be <code>null</code> if
     * {@link #positiveInfinity} is <code>true</code>
     */
    private BigDecimal rightBoundary = null;

    /**
     * <code>true</code> if the left boundary is the negative infinity.
     */
    private boolean negativeInfinity = false;

    /**
     * <code>true</code> if the right boundary is the positive infinity.
     */
    private boolean positiveInfinity = false;

    /**
     * Returns whether this threshold must be negated.
     *
     * @return whether this threshold must be negated.
     */
    public boolean isNegate() {
        return negate;
    }

    /**
     * Sets if this threshold must be negated.
     *
     * @param negateThrehsold
     *            <code>true</code> if must be negated
     */
    void setNegate(final boolean negateThrehsold) {
        this.negate = negateThrehsold;
    }

    /**
     * Returns whether the left boundary is inclusive.
     *
     * @return whether the left boundary is inclusive.
     */
    public boolean isLeftInclusive() {
        return leftInclusive;
    }

    /**
     * Returns whether the right boundary is inclusive.
     *
     * @return whether the right boundary is inclusive.
     */
    public boolean isRightInclusive() {
        return rightInclusive;
    }

    /**
     * Sets if the left boundary must be inclusive.
     *
     * @param leftBoundaryInclusive
     *            <code>true</code> if left boundary is inclusive
     */
    public void setLeftInclusive(final boolean leftBoundaryInclusive) {
        this.leftInclusive = leftBoundaryInclusive;
    }

    /**
     * Sets the left boundary to negative infinity if <code>true</code>.
     * 
     * @param negativeInf
     *            <code>true</code> to set negative infinity
     */
    public void setNegativeInfinity(final boolean negativeInf) {
        negativeInfinity = negativeInf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("negate : ").append(negate)
                .append("\nnegativeInfinity : ").append(negativeInfinity)
                .append("\npositiveInfinity : ").append(positiveInfinity)
                .append("\nleftInclusive : ").append(leftInclusive)
                .append("\nrightInclusive : ").append(rightInclusive)
                .append("\nleftBoundary : ").append(startBoundary)
                .append("\nrightBoundary : ").append(rightBoundary);

        return sb.toString();
    }

    /**
     * Sets the left boundary (numeric).
     * 
     * @param start
     *            The left boundary (numeric)
     */
    public void setLeftBoundary(final BigDecimal start) {
        startBoundary = start;
    }

    /**
     * Sets the right boundary to positive infinity if <code>true</code>.
     * 
     * @param positiveInf
     *            <code>true</code> to set positive infinity
     */
    public void setPositiveInfinity(final boolean positiveInf) {
        positiveInfinity = positiveInf;
    }

    /**
     * Sets the right boundary (numeric).
     * 
     * @param right
     *            The right boundary (numeric)
     */
    public void setRightBoundary(final BigDecimal right) {
        rightBoundary = right;
    }

    /**
     * Sets if the right boundary must be inclusive.
     *
     * @param rightIncl
     *            <code>true</code> if right boundary is inclusive
     */
    public void setRightInclusive(final boolean rightIncl) {
        rightInclusive = rightIncl;
    }

    /**
     * @return The value of the left boundary. <code>null</code> if -inf.
     */
    protected BigDecimal getLeftBoundary() {
        return startBoundary;
    }

    /**
     * @return The value of the right boundary. <code>null</code> if -inf.
     */
    protected BigDecimal getRightBoundary() {
        return rightBoundary;
    }

    /**
     * @return <code>true</code> if the left boundary is -inf.
     */
    protected boolean isNegativeInfinity() {
        return negativeInfinity;
    }

    /**
     * @return <code>true</code> if the right boundary is +inf.
     */
    protected boolean isPositiveInfinity() {
        return positiveInfinity;
    }
}
