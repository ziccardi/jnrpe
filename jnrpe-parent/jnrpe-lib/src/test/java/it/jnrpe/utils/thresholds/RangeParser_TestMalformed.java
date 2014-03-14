package it.jnrpe.utils.thresholds;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RangeParser_TestMalformed {

    @Test( expectedExceptions=InvalidRangeSyntaxException.class)
    public void testExclusiveNegativeInfinite() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("(-inf..+inf", rc);
    }

    @Test( expectedExceptions=InvalidRangeSyntaxException.class)
    public void testExclusivePositiveInfinite() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("-inf..+inf)", rc);
    }

    @Test( expectedExceptions=RangeException.class)
    public void testLeftBoundaryGreaterThanRightBoundary() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("50..10", rc);
    }

    @Test( expectedExceptions=InvalidRangeSyntaxException.class)
    public void testLeftBoundaryGreaterThanRightBoundary_infinity() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("+inf..-inf", rc);
    }

    @Test (expectedExceptions=PrematureEndOfRangeException.class)
    public void testRightIncomplete() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("inf..", rc);
    }

    @Test (expectedExceptions=InvalidRangeSyntaxException.class)
    public void testLeftIncomplete() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("..inf", rc);
    }

    @Test (expectedExceptions=RangeException.class)
    public void testLeftDoubleSign() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("--10..+50", rc);
    }

    @Test (expectedExceptions=RangeException.class)
    public void testRightDoubleSign() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("10..++50", rc);
    }

    @Test (expectedExceptions=InvalidRangeSyntaxException.class)
    public void testBadLeftBoundary() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("1a0..+50", rc);
    }

    @Test (expectedExceptions=InvalidRangeSyntaxException.class)
    public void testBadRightBoundary() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("10..+5a0", rc);
    }
}
