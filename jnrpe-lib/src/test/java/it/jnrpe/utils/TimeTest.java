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

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class TimeTest {
    /**
     * Field SECS.
     * (value is 86400)
     */
    private static final int SECS = 86400;
    
    /**
     * Method unitConvertSecondsToMillisTest.
     */
    @Test
    public void unitConvertSecondsToMillisTest() {
        Assert.assertEquals(86400000, TimeUnit.SECOND.convert(SECS));
    }
    
    /**
     * Method unitConvertSecondsToMinutesTest.
     */
    @Test
    public void unitConvertSecondsToMinutesTest() {
        Assert.assertEquals(24*60, TimeUnit.SECOND.convert(SECS, TimeUnit.MINUTE));
    }
    
    /**
     * Method unitConvertSecondsToHoursTest.
     */
    @Test
    public void unitConvertSecondsToHoursTest() {
        Assert.assertEquals(24, TimeUnit.SECOND.convert(SECS, TimeUnit.HOUR));
    }
    
    /**
     * Method unitConvertSecondsToDayTest.
     */
    @Test
    public void unitConvertSecondsToDayTest() {
        Assert.assertEquals(1, TimeUnit.SECOND.convert(SECS, TimeUnit.DAY));
    }

    /**
     * Method unitConvertDayToSecond.
     */
    @Test
    public void unitConvertDayToSecond() {
        Assert.assertEquals(86400, TimeUnit.DAY.convert(1, TimeUnit.SECOND));
    }
    
    /**
     * Method unitConvertDayToMinute.
     */
    @Test
    public void unitConvertDayToMinute() {
        Assert.assertEquals(60*24, TimeUnit.DAY.convert(1, TimeUnit.MINUTE));
    }
    
    /**
     * Method unitConvertDayToHour.
     */
    @Test
    public void unitConvertDayToHour() {
        Assert.assertEquals(24, TimeUnit.DAY.convert(1, TimeUnit.HOUR));
    }
    
    /**
     * Method unitConvertMinuteToSeconds.
     */
    @Test
    public void unitConvertMinuteToSeconds() {
        Assert.assertEquals(1800, TimeUnit.MINUTE.convert(30, TimeUnit.SECOND));
    }
    
    /**
     * Method unitConvertMinuteToHour.
     */
    @Test
    public void unitConvertMinuteToHour() {
        Assert.assertEquals(1, TimeUnit.MINUTE.convert(90, TimeUnit.HOUR));
    }
}
