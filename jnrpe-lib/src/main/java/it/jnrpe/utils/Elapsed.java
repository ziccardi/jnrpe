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
 * This class converts an amount of time unit in an elapsed time object.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class Elapsed {

    /**
     * Number of seconds.
     */
    private long seconds;
    /**
     * Number of minutes.
     */
    private long minutes;
    /**
     * Number of hours.
     */
    private long hours;
    /**
     * Number of days.
     */
    private long days;

    /**
     * Builds and initializes the elapsed object with the specified amount of timeunit.
     * 
     * @param qty the amount
     * @param unit the time unit
     */
    public Elapsed(final long qty, final TimeUnit unit) {
        init(qty, unit);
    }

    /**
     * Initializes the object.
     * 
     * @param qty the amount
     * @param unit the time unit
     */
    private void init(final long qty, final TimeUnit unit) {
        long millis = unit.convert(qty);
        seconds = TimeUnit.MILLISECOND.convert(millis, TimeUnit.SECOND) % 60;
        minutes = TimeUnit.MILLISECOND.convert(millis, TimeUnit.MINUTE) % 60;
        hours = TimeUnit.MILLISECOND.convert(millis, TimeUnit.HOUR) % 24;
        days = TimeUnit.MILLISECOND.convert(millis, TimeUnit.DAY);
    }

    /**
     * Method getSeconds.
     * @return long
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * Method getMinutes.
     * @return long
     */
    public long getMinutes() {
        return minutes;
    }

    /**
     * Method getHours.
     * @return long
     */
    public long getHours() {
        return hours;
    }

    /**
     * Method getDays.
     * @return long
     */
    public long getDays() {
        return days;
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "Elapsed [seconds=" + seconds + ", minutes=" + minutes + ", hours=" + hours + ", days=" + days + "]";
    }
}
