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

import java.text.MessageFormat;

/**
 * Exception thrown when a range syntax error is found.
 *
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class InvalidRangeSyntaxException extends RangeException {
    /**
    *
    */
    private static final long serialVersionUID = 2036144202685590610L;

    /**
     * Message header if the whold string is unknown.
     */
    private static final String INVALID_RANGE = "Invalid range";

    /**
     * Message header if the whold string is known.
     */
    private static final String INVALID_RANGE_STRING = "Invalid range ''{0}''";

    /**
     * The error message pattern.
     */
    private static final String MESSAGE_PATTERN = "{0} - Stage {1} expected one of ''{2}'' but found ''{3}'' instead.";

    /**
     * Builds the exception specifying the stage and the range string.
     *
     * @param stage
     *            The stage that caused the error.
     * @param found
     *            The string that caused the error.
     */
    public InvalidRangeSyntaxException(final Stage stage, final String found) {
        super(stage, found, null);
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
    public InvalidRangeSyntaxException(final Stage stage, final String found, final String wholeString) {
        super(stage, found, wholeString);
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

        if (expected.length() > 1) {
            return expected.substring(1);
        }
        return "{END OF RANGE}";
    }

    /**
    
     * @return the exception message */
    @Override
    public final String getMessage() {
        final String invalidRange;
        
        String wholeRangeString = getWholeRangeString();
        
        if (wholeRangeString != null) {
            invalidRange = MessageFormat.format(INVALID_RANGE_STRING, wholeRangeString);
        } else {
            invalidRange = INVALID_RANGE;
        }

        return MessageFormat.format(MESSAGE_PATTERN, getFailedStage().getName(), invalidRange, parseExpecting(getFailedStage()), getBadString());
    }
}
