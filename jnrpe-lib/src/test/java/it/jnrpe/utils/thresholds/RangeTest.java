package it.jnrpe.utils.thresholds;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RangeTest {

    @Test
    public void testLeftInfinityInclusive() throws Exception {
        Range range = new Range("-inf..50");
        Assert.assertTrue(range.isValueInside(-1000000));
        Assert.assertTrue(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertTrue(range.isValueInside(new BigDecimal(
                "-99999999999999999999999")));

        Assert.assertFalse(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("50")));
    }

    @Test
    public void tesRightInfinityInclusive() throws Exception {
        Range range = new Range("50..+inf");
        Assert.assertFalse(range.isValueInside(-1000000));
        Assert.assertFalse(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertTrue(range.isValueInside(new BigDecimal(
                "99999999999999999999999")));

        Assert.assertTrue(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("50")));
    }

    @Test
    public void testLeftInfinityExclusive() throws Exception {
        Range range = new Range("-inf..50)");
        Assert.assertTrue(range.isValueInside(-1000000));
        Assert.assertTrue(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertTrue(range.isValueInside(new BigDecimal(
                "-99999999999999999999999")));

        Assert.assertFalse(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("50")));
    }

    @Test
    public void tesRightInfinityExclusive() throws Exception {
        Range range = new Range("(50..+inf");
        Assert.assertFalse(range.isValueInside(-1000000));
        Assert.assertFalse(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertTrue(range.isValueInside(new BigDecimal(
                "99999999999999999999999")));

        Assert.assertTrue(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("50")));
    }

    @Test
    public void testLeftInclusive() throws Exception {
        Range range = new Range("50..150)");
        Assert.assertFalse(range.isValueInside(-1000000));
        Assert.assertFalse(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("150.1")));

        Assert.assertTrue(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("50.0")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("149.999999")));
    }

    @Test
    public void testRightInclusive() throws Exception {
        Range range = new Range("(50..150");
        Assert.assertFalse(range.isValueInside(-1000000));
        Assert.assertFalse(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("150.1")));

        Assert.assertTrue(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("50")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("150.0000")));
    }

    @Test
    public void testNegatedInclusive() throws Exception {
        Range range = new Range("^50..150");
        Assert.assertTrue(range.isValueInside(-1000000));
        Assert.assertTrue(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("150.1")));

        Assert.assertFalse(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("50")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("150.0000")));
    }

    @Test
    public void testNegatedExclusive() throws Exception {
        Range range = new Range("^(50..150)");
        Assert.assertTrue(range.isValueInside(-1000000));
        Assert.assertTrue(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("150.1")));

        Assert.assertFalse(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("50")));
        Assert.assertTrue(range.isValueInside(new BigDecimal("150.0000")));
    }

    @Test
    public void testNegatedAll() throws Exception {
        Range range = new Range("^-inf..+inf");
        Assert.assertFalse(range.isValueInside(-1000000));
        Assert.assertFalse(range.isValueInside(new BigDecimal("49.9999")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("150.1")));

        Assert.assertFalse(range.isValueInside(new BigDecimal("50.1")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("50")));
        Assert.assertFalse(range.isValueInside(new BigDecimal("150.0000")));
    }

    @Test
    public void testNegatedAllInt() throws Exception {
        Range range = new Range("^-inf..+inf");
        Assert.assertFalse(range.isValueInside(-1000000));

        Assert.assertFalse(range.isValueInside(50));
        Assert.assertFalse(range.isValueInside(150));
    }

    @Test
    public void testNegatedAllLong() throws Exception {
        Range range = new Range("^-inf..+inf");
        Assert.assertFalse(range.isValueInside(-1000000L));

        Assert.assertFalse(range.isValueInside(50L));
        Assert.assertFalse(range.isValueInside(150L));
    }

    @Test(expectedExceptions=RangeException.class)
    public void testNull() throws Exception {
        Range range = new Range(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testEvaluateNull() throws Exception {
        Range range = new Range("10..200");
        range.isValueInside(null);
    }

    @Test(expectedExceptions=InvalidRangeSyntaxException.class)
    public void testWhiteString() throws Exception {
        Range range = new Range("   ");
    }

}
