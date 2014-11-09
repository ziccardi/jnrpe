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
package it.jnrpe.utils;

/**
 * Enumeration for time constants.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public enum TimeUnit {

    /**
     * Milliseconds enumeration and converter.
     */
    MILLISECOND(1),
    /**
     * Seconds enumeration and converter.
     */
    SECOND(1000 * MILLISECOND.milliseconds),
    /**
     * Minutes enumeration and converter.
     */
    MINUTE(60 * SECOND.milliseconds),

    /**
     * Hours enumeration and converter.
     */
    HOUR(60 * MINUTE.milliseconds),
    /**
     * Days enumeration and converter.
     */
    DAY(24 * HOUR.milliseconds);

    /**
     * Number of milliseconds represented by the enumeration.
     */
    private long milliseconds;

    /**
     * Builds and initialize the enumeration.
     * 
     * @param value
     *            the number of milliseconds
     */
    private TimeUnit(final long value) {
        milliseconds = value;
    }

    /**
     * Converts the given quantity to milliseconds.
     * 
     * @param qty
     *            the quantity of SECONDS, MINUTES, HOURS or DAYS
    
     * @return the number of milliseconds */
    public long convert(final long qty) {
        return qty * milliseconds;
    }

    /**
     * Convert the input value from this time unit to another.
     * 
     * @param qty
     *            the quantity to be converted.
     * @param to
     *            the destination time unit
    
     * @return the time unit */
    public long convert(final long qty, final TimeUnit to) {
        long millis = convert(qty);
        return millis / to.milliseconds;
    }

    /**
     * Returns the current time in terms of number of TimeUnit since 1st January
     * 1970.
     * 
    
     * @return the current time in terms of number of TimeUnit since 1st January
     *         1970 */
    public long currentTime() {
        return MILLISECOND.convert(System.currentTimeMillis(), this);
    }
}
