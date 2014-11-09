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

import org.testng.Assert;
import org.testng.annotations.Test;

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
        
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS), 86400000);
    }
    
    /**
     * Method unitConvertSecondsToMinutesTest.
     */
    @Test
    public void unitConvertSecondsToMinutesTest() {
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS, TimeUnit.MINUTE), 24*60);
    }
    
    /**
     * Method unitConvertSecondsToHoursTest.
     */
    @Test
    public void unitConvertSecondsToHoursTest() {
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS, TimeUnit.HOUR), 24);
    }
    
    /**
     * Method unitConvertSecondsToDayTest.
     */
    @Test
    public void unitConvertSecondsToDayTest() {
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS, TimeUnit.DAY), 1);
    }

    /**
     * Method unitConvertDayToSecond.
     */
    @Test
    public void unitConvertDayToSecond() {
        Assert.assertEquals(TimeUnit.DAY.convert(1, TimeUnit.SECOND), 86400);
    }
    
    /**
     * Method unitConvertDayToMinute.
     */
    @Test
    public void unitConvertDayToMinute() {
        Assert.assertEquals(TimeUnit.DAY.convert(1, TimeUnit.MINUTE), 60*24);
    }
    
    /**
     * Method unitConvertDayToHour.
     */
    @Test
    public void unitConvertDayToHour() {
        Assert.assertEquals(TimeUnit.DAY.convert(1, TimeUnit.HOUR), 24);
    }
    
    /**
     * Method unitConvertMinuteToSeconds.
     */
    @Test
    public void unitConvertMinuteToSeconds() {
        Assert.assertEquals(TimeUnit.MINUTE.convert(30, TimeUnit.SECOND), 1800);
    }
    
    /**
     * Method unitConvertMinuteToHour.
     */
    @Test
    public void unitConvertMinuteToHour() {
        Assert.assertEquals(TimeUnit.MINUTE.convert(90, TimeUnit.HOUR), 1);
    }
}
