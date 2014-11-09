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

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 */
public class RangeParser_InclusiveTest {

    /**
     * Method testInclusiveOk.
     * @throws RangeException
     */
    @Test
    public void testInclusiveOk() throws RangeException {
        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("10.23..5000.1", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testInclusiveLeftOk.
     * @throws RangeException
     */
    @Test
    public void testInclusiveLeftOk() throws RangeException {
        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("10.23..5000.1)", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testInclusiveRightOk.
     * @throws RangeException
     */
    @Test
    public void testInclusiveRightOk() throws RangeException {
        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("(10.23..5000.1", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testExclusiveBothOk.
     * @throws RangeException
     */
    @Test
    public void testExclusiveBothOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("(10.23..5000.1)", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testExplicitSignBothOk.
     * @throws RangeException
     */
    @Test
    public void testExplicitSignBothOk() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("(-10.23..+5000.1)", rc);

        Assert.assertFalse(rc.isLeftInclusive());
        Assert.assertFalse(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("-10.23"));
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }
}
