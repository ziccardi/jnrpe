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
 * Base class for the range parsing exceptions.
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class RangeException extends BadThresholdException {

    /**
     *
     */
    private static final long serialVersionUID = 8370789724508683948L;

    /**
     * The whole range string.
     */
    private String wholeRangeString = null;

    /**
     * The stage that raised the exception.
     */
    private Stage failedStage;

    /**
     * The erroneous tokens.
     */
    private final String badString;

    /**
     * Generic exception message.
     */
    private String message = null;

    /**
     * Builds and initialize the exception with the error message.
     * 
     * @param errorMessage
     *            The message
     */
    public RangeException(final String errorMessage) {
        super();
        failedStage = null;
        badString = null;
        wholeRangeString = null;
        message = errorMessage;
    }

    /**
     * Builds the exception specifying the stage, the bad tokens and the whole
     * string.
     *
     * @param stage
     *            The stage that caused the error.
     * @param found
     *            The string that caused the error.
     * @param wholeString
     *            The whole range string.
     */
    public RangeException(final Stage stage, final String found, final String wholeString) {
        super();
        failedStage = stage;
        badString = found;
        wholeRangeString = wholeString;
    }

    /**
    
     * @return The whole range string */
    protected final String getWholeRangeString() {
        return wholeRangeString;
    }

    /**
     * Sets the whole range string.
     * 
     * @param rangeString
     *            The whole range string
     */
    final void setWholeRangeString(final String rangeString) {
        wholeRangeString = rangeString;
    }

    /**
    
     * @return The parser stage that failed */
    protected final Stage getFailedStage() {
        return failedStage;
    }

    /**
    
     * @return the erroneous tokens */
    protected final String getBadString() {
        return badString;
    }

    /**
     * Utility method for error messages.
     *
     * @param stage
     *            the stage to ask for expected tokens.
    
     * @return The list of expected tokens */
    private static String parseExpecting(final Stage stage) {
        StringBuilder expected = new StringBuilder();

        for (String key : stage.getTransitionNames()) {
            expected.append(',').append(stage.getTransition(key).expects());
        }

        return expected.substring(1);
    }

    /**
     * Returns the expected token for the failed stage.
     * 
    
     * @return the expected token for the failed stage. */
    protected final String getExpectedTokens() {
        return parseExpecting(failedStage);
    }

    /**
     * Sets the stage that failed.
     * 
     * @param stage
     *            The failed stage
     */
    final void setFailedStage(final Stage stage) {
        failedStage = stage;
    }

    /**
    
     * @return the error message. */
    @Override
    public String getMessage() {
        return message;
    }
}
