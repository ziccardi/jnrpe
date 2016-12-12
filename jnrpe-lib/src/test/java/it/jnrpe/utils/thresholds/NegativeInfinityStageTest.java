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

/**
 */
public class NegativeInfinityStageTest {
    /**
     * Method testCanParseNull.
     */
    @Test
    public void testCanParseNull() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertFalse(stage.canParse(null));
    }

    /**
     * Method testCanParseWithoutMinus.
     */
    @Test
    public void testCanParseWithoutMinus() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertTrue(stage.canParse("inf"));
    }

    /**
     * Method testCanParseWithMinus.
     */
    @Test
    public void testCanParseWithMinus() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertTrue(stage.canParse("-inf"));
    }

    /**
     * Method testExpect.
     */
    @Test
    public void testExpect() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertEquals(stage.expects(), "[-]inf");
    }

    /**
     * Method testParseWithSignOk.
     * @throws InvalidRangeSyntaxException
     */
    @Test
    public void testParseWithSignOk() throws InvalidRangeSyntaxException {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertEquals(stage.parse("-inf..80", new RangeConfig()), "..80");
    }

    /**
     * Method testParseWithoutSignOk.
     * @throws InvalidRangeSyntaxException
     */
    @Test
    public void testParseWithoutSignOk() throws InvalidRangeSyntaxException {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertEquals(stage.parse("inf..80", new RangeConfig()), "..80");
    }


    /**
     * Method testParseKo.
     * @throws InvalidRangeSyntaxException
     */
    @Test
    public void testParseKo() throws InvalidRangeSyntaxException {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        Assert.assertFalse(stage.canParse("50..80"));
    }
}
