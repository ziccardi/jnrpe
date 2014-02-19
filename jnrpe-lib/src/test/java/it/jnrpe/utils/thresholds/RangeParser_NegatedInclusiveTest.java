package it.jnrpe.utils.thresholds;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RangeParser_NegatedInclusiveTest {

    @Test
    public void testNegatedInclusiveOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^10.23..5000.1", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    @Test
    public void testNegatedInclusiveLeftOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^10.23..5000.1)", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    @Test
    public void testNegatedInclusiveRightOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^(10.23..5000.1", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    @Test
    public void testNegatedExclusiveBothOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^(10.23..5000.1)", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }
}
