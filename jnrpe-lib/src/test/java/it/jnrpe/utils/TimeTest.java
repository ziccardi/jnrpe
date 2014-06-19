package it.jnrpe.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TimeTest {
    private final static int SECS = 86400;
    
    @Test
    public void unitConvertSecondsToMillisTest() {
        
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS), 86400000);
    }
    
    @Test
    public void unitConvertSecondsToMinutesTest() {
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS, TimeUnit.MINUTE), 24*60);
    }
    
    @Test
    public void unitConvertSecondsToHoursTest() {
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS, TimeUnit.HOUR), 24);
    }
    
    @Test
    public void unitConvertSecondsToDayTest() {
        Assert.assertEquals(TimeUnit.SECOND.convert(SECS, TimeUnit.DAY), 1);
    }

    @Test
    public void unitConvertDayToSecond() {
        Assert.assertEquals(TimeUnit.DAY.convert(1, TimeUnit.SECOND), 86400);
    }
    
    @Test
    public void unitConvertDayToMinute() {
        Assert.assertEquals(TimeUnit.DAY.convert(1, TimeUnit.MINUTE), 60*24);
    }
    
    @Test
    public void unitConvertDayToHour() {
        Assert.assertEquals(TimeUnit.DAY.convert(1, TimeUnit.HOUR), 24);
    }
    
    @Test
    public void unitConvertMinuteToSeconds() {
        Assert.assertEquals(TimeUnit.MINUTE.convert(30, TimeUnit.SECOND), 1800);
    }
    
    @Test
    public void unitConvertMinuteToHour() {
        Assert.assertEquals(TimeUnit.MINUTE.convert(90, TimeUnit.HOUR), 1);
    }
}
