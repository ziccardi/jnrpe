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
