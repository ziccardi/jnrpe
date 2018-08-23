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
import org.junit.Assert;

/**
 */
public class NegateStageTest {

    /**
     * Method testCanParseNull.
     */
    @Test
    public void testCanParseNull() {
        NegateStage stage = new NegateStage();
        Assert.assertFalse(stage.canParse(null));
    }

    /**
     * Method testCanParseOk.
     */
    @Test
    public void testCanParseOk() {
        NegateStage stage = new NegateStage();
        Assert.assertTrue(stage.canParse("^"));
    }

    /**
     * Method testCanParseKo.
     */
    @Test
    public void testCanParseKo() {
        NegateStage stage = new NegateStage();
        Assert.assertFalse(stage.canParse("("));
    }

    /**
     * Method testExpect.
     */
    @Test
    public void testExpect() {
        NegateStage stage = new NegateStage();
        Assert.assertEquals(stage.expects(), "^");
    }

    /**
     * Method testParseOk.
     * @throws InvalidRangeSyntaxException
     */
    @Test
    public void testParseOk() throws InvalidRangeSyntaxException {
        NegateStage stage = new NegateStage();
        Assert.assertEquals(stage.parse("^50..80", new RangeConfig()), "50..80");
    }

    /**
     * Method testParseKo.
     * @throws InvalidRangeSyntaxException
     */
    @Test
    public void testParseKo() throws InvalidRangeSyntaxException {
        NegateStage stage = new NegateStage();
        Assert.assertFalse(stage.canParse("50..80"));
    }

}
