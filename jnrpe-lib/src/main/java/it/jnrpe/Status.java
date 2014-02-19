/*
 * Copyright (c) 2008 Massimiliano Ziccardi Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package it.jnrpe;

/**
 * Enumeration of all the supported status.
 *
 * @author Massimiliano Ziccardi
 */
public enum Status {
    /**
     * Service status 'OK'.
     */
    OK(0, 0),
    /**
     * Service status 'WARNING'.
     */
    WARNING(1, 2),
    /**
     * Service status 'CRITICAL'.
     */
    CRITICAL(2, 3),
    /**
     * Service status 'UNKNOWN'.
     */
    UNKNOWN(3, 1);

    /**
     * Integer constant for the OK status.
     */
    private static final int OK_STATUS = 0;
    /**
     * Integer constant for the WARNING status.
     */
    private static final int WARNING_STATUS = 1;
    /**
     * Integer constant for the CRITICAL status.
     */
    private static final int CRITICAL_STATUS = 2;
    /**
     * Integer constant for the UNKNOWN status.
     */
    private static final int UNKNOWN_STATUS = 3;

    /**
     * The enumeration int value.
     */
    private final int intValue;

    /**
     * The serverity value.
     */
    private final int severityValue;

    /**
     * Builds an enumeration with the given int value.
     *
     * @param iValue
     *            The value
     * @param severity
     *            The severity of the status
     */
    Status(final int iValue, final int severity) {
        intValue = iValue;
        severityValue = severity;
    }

    /**
     * Returns the int value of the enum.
     *
     * @return The int value
     */
    public int intValue() {
        return intValue;
    }

    /**
     * Returns the enum corresponding with the given int value.
     *
     * @param intValue
     *            The int value to be converted
     * @return A {@link Status} enum. Null if not found
     */
    public static Status fromIntValue(final int intValue) {
        switch (intValue) {
        case OK_STATUS:
            return OK;
        case WARNING_STATUS:
            return WARNING;
        case CRITICAL_STATUS:
            return CRITICAL;
        case UNKNOWN_STATUS:
        default:
            return UNKNOWN;
        }
    }

    /**
     * @return The severity as an int value (higher is more severe)
     */
    public int getSeverity() {
        return severityValue;
    }
}
