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

import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricBuilder;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 */
@SuppressWarnings("deprecation")
public class ThresholdUtilTest {

    private Metric buildMetric(int value) {
        return MetricBuilder.forMetric("TEST_METRIC)")
                .withValue(value)
                .build();
    }
    
    private Metric buildMetric(long value) {
        return MetricBuilder.forMetric("TEST_METRIC)")
                .withValue(value)
                .build();
    }
    
    private Metric buildMetric(BigDecimal value) {
        return MetricBuilder.forMetric("TEST_METRIC)")
                .withValue(value)
                .build();
    }
    
    /**
     * Method testSimpleThreshold.
     * @throws BadThresholdException
     */
    @Test
    public void testSimpleThreshold() throws BadThresholdException {
        Assert.assertTrue(ThresholdUtil.isValueInRange("10", buildMetric(5)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10", buildMetric(0)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10", buildMetric(10)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10", buildMetric(-1)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10", buildMetric(11)));

    }

    /**
     * Method testToInfinity.
     * @throws BadThresholdException
     */
    @Test
    public void testToInfinity() throws BadThresholdException {
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:", buildMetric(10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:", buildMetric(100)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:", buildMetric(1000)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:", buildMetric(9)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:", buildMetric(-11)));
    }

    /**
     * Method testToInfinityExplicit.
     * @throws BadThresholdException
     */
    @Test
    public void testToInfinityExplicit() throws BadThresholdException {
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:~", buildMetric(10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:~", buildMetric(100)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:~", buildMetric(1000)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:~", buildMetric(9)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:~", buildMetric(-11)));
    }

    /**
     * Method testNegativeInfinity.
     * @throws BadThresholdException
     */
    @Test
    public void testNegativeInfinity() throws BadThresholdException {
        Assert.assertTrue(ThresholdUtil.isValueInRange("~:10", buildMetric(10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("~:10", buildMetric(-10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("~:10", buildMetric(-100)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("~:10", buildMetric(-1000)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("~:10", buildMetric(11)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("~:10", buildMetric(100)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("~:10", buildMetric(1000)));
    }

    /**
     * Method testBounded.
     * @throws BadThresholdException
     */
    @Test
    public void testBounded() throws BadThresholdException {
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:20", buildMetric(10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:20", buildMetric(15)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("10:20", buildMetric(20)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:20", buildMetric(9)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:20", buildMetric(21)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("10:20", buildMetric(-10)));
    }

    /**
     * Method testNegatedBounded.
     * @throws BadThresholdException
     */
    @Test
    public void testNegatedBounded() throws BadThresholdException {
        Assert.assertFalse(ThresholdUtil.isValueInRange("@10:20", buildMetric(10)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("@10:20", buildMetric(15)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("@10:20", buildMetric(20)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("@10:20", buildMetric(9)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("@10:20", buildMetric(21)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("@10:20", buildMetric(-10)));
    }

    /**
     * Method testMalformedNegation.
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = BadThresholdException.class)
    public void testMalformedNegation() throws BadThresholdException {
        ThresholdUtil.isValueInRange("10:@20", buildMetric(10));
    }

    /**
     * Method testBadSeparator.
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = BadThresholdException.class)
    public void testBadSeparator() throws BadThresholdException {
        ThresholdUtil.isValueInRange("10:20:", buildMetric(10));
    }

    /**
     * Method testEmptyNumbers.
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = BadThresholdException.class)
    public void testEmptyNumbers() throws BadThresholdException {
        ThresholdUtil.isValueInRange(":", buildMetric(10));
    }

    /**
     * Method testEmptyNumbersJustSign.
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = BadThresholdException.class)
    public void testEmptyNumbersJustSign() throws BadThresholdException {
        ThresholdUtil.isValueInRange("+:", buildMetric(10));
    }

    /**
     * Method testMissingLeft.
     * @throws BadThresholdException
     */
    @Test
    public void testMissingLeft() throws BadThresholdException {
        Assert.assertTrue(ThresholdUtil.isValueInRange(":20", buildMetric(10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange(":20", buildMetric(20)));
        Assert.assertFalse(ThresholdUtil.isValueInRange(":20", buildMetric(21)));
        Assert.assertFalse(ThresholdUtil.isValueInRange(":20", buildMetric(-5000)));
    }

    /**
     * Method testMissingRight.
     * @throws BadThresholdException
     */
    @Test
    public void testMissingRight() throws BadThresholdException {
        Assert.assertFalse(ThresholdUtil.isValueInRange("20:", buildMetric(10)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("20:", buildMetric(20)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("20:", buildMetric(21)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("20:", buildMetric(-5000)));
    }

    /**
     * Method testMissingRightLong.
     * @throws BadThresholdException
     */
    @Test
    public void testMissingRightLong() throws BadThresholdException {
        Assert.assertFalse(ThresholdUtil.isValueInRange("20:", buildMetric(10L)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("20:", buildMetric(20L)));
        Assert.assertTrue(ThresholdUtil.isValueInRange("20:", buildMetric(21L)));
        Assert.assertFalse(ThresholdUtil.isValueInRange("20:", buildMetric(-5000L)));
    }

    /**
     * Method testMissingRightBigDecimal.
     * @throws BadThresholdException
     */
    @Test
    public void testMissingRightBigDecimal() throws BadThresholdException {
        Assert.assertFalse(ThresholdUtil.isValueInRange("20:", buildMetric(new BigDecimal(10))));
        Assert.assertTrue(ThresholdUtil.isValueInRange("20:", buildMetric(new BigDecimal(20))));
        Assert.assertTrue(ThresholdUtil.isValueInRange("20:", buildMetric(new BigDecimal(21))));
        Assert.assertFalse(ThresholdUtil.isValueInRange("20:", buildMetric(new BigDecimal(-5000))));
    }

}
