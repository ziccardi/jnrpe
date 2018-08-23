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

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 */
public class RangeParser_NegatedInclusiveTest {

    /**
     * Method testNegatedInclusiveOk.
     * @throws RangeException
     */
    @Test
    public void testNegatedInclusiveOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^10.23..5000.1", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(new BigDecimal("5000.1"), rc.getRightBoundary());
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testNegatedInclusiveLeftOk.
     * @throws RangeException
     */
    @Test
    public void testNegatedInclusiveLeftOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^10.23..5000.1)", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(new BigDecimal("10.23"), rc.getLeftBoundary());
        Assert.assertEquals(new BigDecimal("5000.1"), rc.getRightBoundary());
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testNegatedInclusiveRightOk.
     * @throws RangeException
     */
    @Test
    public void testNegatedInclusiveRightOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^(10.23..5000.1", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(new BigDecimal("10.23"), rc.getLeftBoundary());
        Assert.assertEquals(new BigDecimal("5000.1"), rc.getRightBoundary());
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testNegatedExclusiveBothOk.
     * @throws RangeException
     */
    @Test
    public void testNegatedExclusiveBothOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("^(10.23..5000.1)", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertTrue(rc.isNegate());
        Assert.assertEquals(new BigDecimal("10.23"), rc.getLeftBoundary());
        Assert.assertEquals(new BigDecimal("5000.1"), rc.getRightBoundary());
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }
}
