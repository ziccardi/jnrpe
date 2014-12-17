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

import it.jnrpe.plugins.Metric;
import it.jnrpe.utils.BadThresholdException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.math.BigDecimal;

/**
 * Utility class for evaluating thresholds. This class represent a Threshold
 * specified using the old Nagios syntax.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class LegacyRange {
    
    private Prefixes prefix = Prefixes.RAW;
    
    /**
     * When the current state is 'MINVAL', that means that the value we are
     * parsing is the definition of the minimum value.
     */
    private static final int MINVAL = 0;

    /**
     * When the current state is 'MAXVAL', that means that the value we are is
     * the definition of the maximum value.
     */
    private static final int MAXVAL = 1;

    /**
     * When the current state is 'END', that means that we have finished parsing
     * the threshold definition.
     */
    private static final int END = 99;

    /**
     * The mimimum value as parsed from the threshold definition.
     */
    private BigDecimal minVal = null;

    /**
     * The maximum value as parsed from the threshold definition.
     */
    private BigDecimal maxVal = null;

    /**
     * <code>true</code> if the threshold is negated.
     */
    private boolean negateThreshold = false;

    /**
     * The current state of the threshold parser.
     */
    private int curState = MINVAL;

    /**
     * The unparsed threshold string.
     */
    private final String thresholdString;

    /**
     * Builds the object with the specified range.
     * 
     * @param threshold
     *            The range
    
     * @throws BadThresholdException
     *             - */
    public LegacyRange(final String threshold) throws BadThresholdException {
        thresholdString = threshold;
        parseRange();
    }
    
    public LegacyRange(final String threshold, final Prefixes prefix) throws BadThresholdException {
        thresholdString = threshold;
        this.prefix = prefix;
        parseRange();
    }

    /**
     * Parses the range definition to evaluate the minimum and maximum
     * thresholds.
     * 
    
     * @throws BadThresholdException
     *             - */
    private void parseRange() throws BadThresholdException {

        PushbackReader reader = new PushbackReader(new StringReader(thresholdString));

        StringBuilder currentParsedBuffer = new StringBuilder();

        byte b = 0;

        try {
            while ((b = (byte) reader.read()) != -1) {
                currentParsedBuffer.append((char) b);
                if (b == '@') {
                    if (curState != MINVAL) {
                        throw new BadThresholdException("Unparsable threshold '" + thresholdString + "'. Error at char "
                                + currentParsedBuffer.length() + ": the '@' should not be there.");
                    }
                    negateThreshold = true;
                    continue;
                }
                if (b == ':') {

                    switch (curState) {
                    case MINVAL:
                        if (minVal == null) {
                            minVal = new BigDecimal(0);
                        }
                        curState = MAXVAL;
                        currentParsedBuffer = new StringBuilder();
                        continue;
                    case MAXVAL:
                        throw new BadThresholdException("Unparsable threshold '" + thresholdString + "'. Error at char "
                                + currentParsedBuffer.length() + ": the ':' should not be there.");
                        // m_iCurState = END;
                        // continue;
                    default:
                        curState = MAXVAL;
                    }
                }
                if (b == '~') {
                    switch (curState) {
                    case MINVAL:
                        minVal = new BigDecimal(Integer.MIN_VALUE);
                        currentParsedBuffer = new StringBuilder();
                        // m_iCurState = MAXVAL;
                        continue;
                    case MAXVAL:
                        maxVal = new BigDecimal(Integer.MAX_VALUE);
                        curState = END;
                        currentParsedBuffer = new StringBuilder();
                        continue;
                    default:
                    }

                }

                StringBuilder numberBuffer = new StringBuilder();

                // while (i < vBytes.length &&
                // Character.isDigit((char)vBytes[i]))

                do {
                    numberBuffer.append((char) b);
                } while (((b = (byte) reader.read()) != -1) && (Character.isDigit((char) b) || b == '+' || b == '-' || b == '.'));

                if (b != -1) {
                    reader.unread(b);
                }

                String numberString = numberBuffer.toString();
                if (numberString.trim().length() == 0 || "+".equals(numberString) || "-".equals(numberString)) {
                    throw new BadThresholdException("A number was expected after '" + currentParsedBuffer.toString()
                            + "', but an empty string was found");
                }

                switch (curState) {
                case MINVAL:
                    try {
                        minVal = new BigDecimal(numberString.trim());
                    } catch (NumberFormatException nfe) {
                        throw new BadThresholdException("Expected a number but found '" + numberString + "' instead [" + thresholdString + "]", nfe);
                    }
                    currentParsedBuffer = new StringBuilder();
                    continue;
                case MAXVAL:
                    try {
                        maxVal = new BigDecimal(numberString.trim());
                    } catch (NumberFormatException nfe) {
                        throw new BadThresholdException("Expected a number but found '" + numberString + "' instead", nfe);
                    }
                    currentParsedBuffer = new StringBuilder();
                    continue;
                default:
                    curState = END;
                    currentParsedBuffer = new StringBuilder();
                }
                // if (i < vBytes.length)
                // i-=2;
            }
        } catch (IOException ioe) {
            // Intentionally empty...
        }

        if (curState == MINVAL) {
            maxVal = minVal;
            minVal = new BigDecimal(0);
        }

        if (curState == MAXVAL && maxVal == null && !thresholdString.isEmpty() && thresholdString.charAt(0) == ':') {
            throw new BadThresholdException("At least one of maximum or minimum " + "value must me specified (" + thresholdString + ")");
        }

    }

    /**
     * Returns <code>true</code> if the value falls inside the range.
     * 
     * @param value
     *            The value
     * @param prefix
     *            The prefix that identifies the multiplier
    
     * @return <code>true</code> if the value falls inside the range.
     *         <code>false</code> otherwise. */
    public final boolean isValueInside(final Metric metric) {
        
        BigDecimal value = metric.getMetricValue(prefix);
        
        boolean bRes = true;
        // Sets the minimum value of the range
        if (minVal != null) {
            bRes = bRes && (value.compareTo(minVal) >= 0);
        }
        // Sets the maximum value of the range
        if (maxVal != null) {
            bRes = bRes && (value.compareTo(maxVal) <= 0);
        }
        if (negateThreshold) {
            return !bRes;
        }
        return bRes;
    }

    /**
     * @return The original unparsed threshold string. 
     */
    final String getThresholdString() {
        return thresholdString;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "LegacyRange [minVal=" + minVal + ", maxVal=" + maxVal + ", negateThreshold=" + negateThreshold + ", curState=" + curState
                + ", thresholdString=" + thresholdString + "]";
    }
}
