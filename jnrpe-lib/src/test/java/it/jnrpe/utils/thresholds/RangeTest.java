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
package it.jnrpe.utils.thresholds;

import it.jnrpe.plugins.Metric;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 */
public class RangeTest {

    private static Metric metricWithValue(String value) {
        return Metric.forMetric("dummy", BigDecimal.class)
                .withValue(new BigDecimal(value)).build();
//        return MetricBuilder.forMetric("dummy")
//                .withValue(new BigDecimal(value)).build();
    }
    
    /**
     * Method testLeftInfinityInclusive.
     * 
     * @throws Exception
     */
    @Test
    public void testLeftInfinityInclusive() throws Exception {
        Range range = new Range("-inf..50");
        Assert.assertTrue(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertTrue(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertTrue(range.isValueInside(metricWithValue("-99999999999999999999999")));

        Assert.assertFalse(range.isValueInside(metricWithValue("50.1")));
        Assert.assertTrue(range.isValueInside(metricWithValue("50")));
    }

    /**
     * Method tesRightInfinityInclusive.
     * 
     * @throws Exception
     */
    @Test
    public void tesRightInfinityInclusive() throws Exception {
        Range range = new Range("50..+inf");
        Assert.assertFalse(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertFalse(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertTrue(range.isValueInside(metricWithValue("99999999999999999999999")));

        Assert.assertTrue(range.isValueInside(metricWithValue("50.1")));
        Assert.assertTrue(range.isValueInside(metricWithValue("50")));
    }

    /**
     * Method testLeftInfinityExclusive.
     * 
     * @throws Exception
     */
    @Test
    public void testLeftInfinityExclusive() throws Exception {
        Range range = new Range("-inf..50)");
        Assert.assertTrue(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertTrue(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertTrue(range.isValueInside(metricWithValue("-99999999999999999999999")));

        Assert.assertFalse(range.isValueInside(metricWithValue("50.1")));
        Assert.assertFalse(range.isValueInside(metricWithValue("50")));
    }

    /**
     * Method tesRightInfinityExclusive.
     * 
     * @throws Exception
     */
    @Test
    public void tesRightInfinityExclusive() throws Exception {
        Range range = new Range("(50..+inf");
        Assert.assertFalse(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertFalse(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertTrue(range.isValueInside(metricWithValue("99999999999999999999999")));

        Assert.assertTrue(range.isValueInside(metricWithValue("50.1")));
        Assert.assertFalse(range.isValueInside(metricWithValue("50")));
    }

    /**
     * Method testLeftInclusive.
     * 
     * @throws Exception
     */
    @Test
    public void testLeftInclusive() throws Exception {
        Range range = new Range("50..150)");
        Assert.assertFalse(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertFalse(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertFalse(range.isValueInside(metricWithValue("150.1")));

        Assert.assertTrue(range.isValueInside(metricWithValue("50.1")));
        Assert.assertTrue(range.isValueInside(metricWithValue("50.0")));
        Assert.assertTrue(range.isValueInside(metricWithValue("149.999999")));
    }

    /**
     * Method testRightInclusive.
     * 
     * @throws Exception
     */
    @Test
    public void testRightInclusive() throws Exception {
        Range range = new Range("(50..150");
        Assert.assertFalse(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertFalse(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertFalse(range.isValueInside(metricWithValue("150.1")));

        Assert.assertTrue(range.isValueInside(metricWithValue("50.1")));
        Assert.assertFalse(range.isValueInside(metricWithValue("50")));
        Assert.assertTrue(range.isValueInside(metricWithValue("150.0000")));
    }

    /**
     * Method testNegatedInclusive.
     * 
     * @throws Exception
     */
    @Test
    public void testNegatedInclusive() throws Exception {
        Range range = new Range("^50..150");
        Assert.assertTrue(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertTrue(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertTrue(range.isValueInside(metricWithValue("150.1")));

        Assert.assertFalse(range.isValueInside(metricWithValue("50.1")));
        Assert.assertFalse(range.isValueInside(metricWithValue("50")));
        Assert.assertFalse(range.isValueInside(metricWithValue("150.0000")));
    }

    /**
     * Method testNegatedExclusive.
     * 
     * @throws Exception
     */
    @Test
    public void testNegatedExclusive() throws Exception {
        Range range = new Range("^(50..150)");
        Assert.assertTrue(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertTrue(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertTrue(range.isValueInside(metricWithValue("150.1")));

        Assert.assertFalse(range.isValueInside(metricWithValue("50.1")));
        Assert.assertTrue(range.isValueInside(metricWithValue("50")));
        Assert.assertTrue(range.isValueInside(metricWithValue("150.0000")));
    }

    /**
     * Method testNegatedAll.
     * 
     * @throws Exception
     */
    @Test
    public void testNegatedAll() throws Exception {
        Range range = new Range("^-inf..+inf");
        Assert.assertFalse(range.isValueInside(metricWithValue("-1000000")));
        Assert.assertFalse(range.isValueInside(metricWithValue("49.9999")));
        Assert.assertFalse(range.isValueInside(metricWithValue("150.1")));

        Assert.assertFalse(range.isValueInside(metricWithValue("50.1")));
        Assert.assertFalse(range.isValueInside(metricWithValue("50")));
        Assert.assertFalse(range.isValueInside(metricWithValue("150.0000")));
    }

    /**
     * Method testNegatedAllInt.
     * 
     * @throws Exception
     */
    @Test
    public void testNegatedAllInt() throws Exception {
        Range range = new Range("^-inf..+inf");
        Assert.assertFalse(range.isValueInside(metricWithValue("-1000000")));

        Assert.assertFalse(range.isValueInside(metricWithValue("50")));
        Assert.assertFalse(range.isValueInside(metricWithValue("150")));
    }

    /**
     * Method testNull.
     * 
     * @throws Exception
     */
    @Test(expected = RangeException.class)
    public void testNull() throws Exception {
        new Range(null);
    }

    /**
     * Method testEvaluateNull.
     * 
     * @throws Exception
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateNull() throws Exception {
        Range range = new Range("10..200");
        range.isValueInside(null);
    }

    /**
     * Method testWhiteString.
     * 
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testWhiteString() throws Exception {
        new Range("   ");
    }

}
