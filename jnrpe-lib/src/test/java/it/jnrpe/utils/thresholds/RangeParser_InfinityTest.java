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
public class RangeParser_InfinityTest {

    /**
     * Method testImplicitNegativeInfinity.
     * @throws RangeException
     */
    @Test
    public void testImplicitNegativeInfinity() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("inf..5000.1", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertNull(rc.getLeftBoundary());
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertTrue(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testExlicitNegativeInfinity.
     * @throws RangeException
     */
    @Test
    public void testExlicitNegativeInfinity() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("-inf..5000.1", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertNull(rc.getLeftBoundary());
        Assert.assertEquals(rc.getRightBoundary(), new BigDecimal("5000.1"));
        Assert.assertTrue(rc.isNegativeInfinity());
        Assert.assertFalse(rc.isPositiveInfinity());
    }

    /**
     * Method testImplicitPositiveInfinity.
     * @throws RangeException
     */
    @Test
    public void testImplicitPositiveInfinity() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("50..inf", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("50"));
        Assert.assertNull(rc.getRightBoundary());
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertTrue(rc.isPositiveInfinity());
    }

    /**
     * Method testExplicitPositiveInfinity.
     * @throws RangeException
     */
    @Test
    public void testExplicitPositiveInfinity() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("50..+inf", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertEquals(rc.getLeftBoundary(), new BigDecimal("50"));
        Assert.assertNull(rc.getRightBoundary());
        Assert.assertFalse(rc.isNegativeInfinity());
        Assert.assertTrue(rc.isPositiveInfinity());
    }

    /**
     * Method testImplicitPositiveNegativeInfinity.
     * @throws RangeException
     */
    @Test
    public void testImplicitPositiveNegativeInfinity() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("inf..inf", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertNull(rc.getLeftBoundary());
        Assert.assertNull(rc.getRightBoundary());
        Assert.assertTrue(rc.isNegativeInfinity());
        Assert.assertTrue(rc.isPositiveInfinity());
    }

    /**
     * Method testExplicitPositiveNegativeInfinity.
     * @throws RangeException
     */
    @Test
    public void testExplicitPositiveNegativeInfinity() throws RangeException {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("-inf..+inf", rc);

        Assert.assertTrue(rc.isLeftInclusive());
        Assert.assertTrue(rc.isRightInclusive());
        Assert.assertFalse(rc.isNegate());
        Assert.assertNull(rc.getLeftBoundary());
        Assert.assertNull(rc.getRightBoundary());
        Assert.assertTrue(rc.isNegativeInfinity());
        Assert.assertTrue(rc.isPositiveInfinity());
    }
}
