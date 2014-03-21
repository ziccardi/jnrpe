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

import org.testng.annotations.Test;

import it.jnrpe.utils.thresholds.BracketStage.ClosedBracketStage;
import it.jnrpe.utils.thresholds.BracketStage.OpenBracketStage;
import static org.testng.Assert.*;

public class NegateStageTest {

    @Test
    public void testCanParseNull() {
        NegateStage stage = new NegateStage();
        assertFalse(stage.canParse(null));
    }

    @Test
    public void testCanParseOk() {
        NegateStage stage = new NegateStage();
        assertTrue(stage.canParse("^"));
    }

    @Test
    public void testCanParseKo() {
        NegateStage stage = new NegateStage();
        assertFalse(stage.canParse("("));
    }

    @Test
    public void testExpect() {
        NegateStage stage = new NegateStage();
        assertEquals(stage.expects(), "^");
    }

    @Test
    public void testParseOk() throws InvalidRangeSyntaxException {
        NegateStage stage = new NegateStage();
        assertEquals(stage.parse("^50..80", new RangeConfig()), "50..80");
    }

    @Test
    public void testParseKo() throws InvalidRangeSyntaxException {
        NegateStage stage = new NegateStage();
        assertFalse(stage.canParse("50..80"));
    }

}
