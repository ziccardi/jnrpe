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
 * Exception thrown when the parse string is not complete.
 *
 * @author Massimiliano Ziccardi
 */
public class PrematureEndOfRangeException extends RangeException {
    /**
    *
    */
    private static final long serialVersionUID = 2036144202685590610L;

    /**
     * Message pattern.
     */
    private static final String MESSAGE_PATTERN = "Premature end of range parsing ''{0}''. " + "Stage ''{1}'' expected one of ''{2}''";

    /**
     * Builds the exception specifying the stage and the range string.
     *
     * @param stage
     *            The stage that caused the error.
     * @param wholeString
     *            The received range string.
     */
    PrematureEndOfRangeException(final Stage stage, final String wholeString) {
        super(stage, null, wholeString);
    }

    /**
     * @return the exception message
     */
    @Override
    public final String getMessage() {
        return MessageFormat.format(MESSAGE_PATTERN, getWholeRangeString(), getFailedStage().getName(), getExpectedTokens());
    }
}
