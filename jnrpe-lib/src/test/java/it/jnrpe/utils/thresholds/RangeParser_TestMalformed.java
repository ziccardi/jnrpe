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

import org.junit.Test;

/**
 */
public class RangeParser_TestMalformed {

    /**
     * Method testExclusiveNegativeInfinite.
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testExclusiveNegativeInfinite() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("(-inf..+inf", rc);
    }

    /**
     * Method testExclusivePositiveInfinite.
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testExclusivePositiveInfinite() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("-inf..+inf)", rc);
    }

    /**
     * Method testLeftBoundaryGreaterThanRightBoundary.
     * @throws Exception
     */
    @Test(expected = RangeException.class)
    public void testLeftBoundaryGreaterThanRightBoundary() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("50..10", rc);
    }

    /**
     * Method testLeftBoundaryGreaterThanRightBoundary_infinity.
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testLeftBoundaryGreaterThanRightBoundary_infinity() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("+inf..-inf", rc);
    }

    /**
     * Method testRightIncomplete.
     * @throws Exception
     */
    @Test(expected = PrematureEndOfRangeException.class)
    public void testRightIncomplete() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("inf..", rc);
    }

    /**
     * Method testLeftIncomplete.
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testLeftIncomplete() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("..inf", rc);
    }

    /**
     * Method testLeftDoubleSign.
     * @throws Exception
     */
    @Test(expected = RangeException.class)
    public void testLeftDoubleSign() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("--10..+50", rc);
    }

    /**
     * Method testRightDoubleSign.
     * @throws Exception
     */
    @Test(expected = RangeException.class)
    public void testRightDoubleSign() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("10..++50", rc);
    }

    /**
     * Method testBadLeftBoundary.
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testBadLeftBoundary() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("1a0..+50", rc);
    }

    /**
     * Method testBadRightBoundary.
     * @throws Exception
     */
    @Test(expected = InvalidRangeSyntaxException.class)
    public void testBadRightBoundary() throws Exception {

        RangeConfig rc = new RangeConfig();

        RangeStringParser.parse("10..+5a0", rc);
    }
}
