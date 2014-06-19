package it.jnrpe.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestElapsed {

    @Test
    public void testParsingMillis() {
        // 5 DAYS + 15 HOURS + 12 MINUTES + 24 SECONDS + 500 MILLIS
        long millis = TimeUnit.DAY.convert(5) + TimeUnit.HOUR.convert(15) + TimeUnit.MINUTE.convert(12) + TimeUnit.SECOND.convert(24)
                + TimeUnit.MILLISECOND.convert(500);

        Elapsed elapsed = new Elapsed(millis, TimeUnit.MILLISECOND);
        
        Assert.assertEquals(elapsed.getDays(), 5);
        Assert.assertEquals(elapsed.getHours(), 15);
        Assert.assertEquals(elapsed.getMinutes(), 12);
        Assert.assertEquals(elapsed.getSeconds(), 24);
    }
}
