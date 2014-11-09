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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import it.jnrpe.utils.thresholds.NumberBoundaryStage.LeftBoundaryStage;

import org.testng.annotations.Test;

/**
 */
public class NumberBoundaryStageTest {
    /**
     * Method testCanParseNull.
     */
    @Test
    public void testCanParseNull() {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertFalse(stage.canParse(null));
    }

    /**
     * Method testCanParseWithoutSign.
     */
    @Test
    public void testCanParseWithoutSign() {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertTrue(stage.canParse("50"));
    }

    /**
     * Method testCanParseWithSign.
     */
    @Test
    public void testCanParseWithSign() {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertTrue(stage.canParse("-80909"));
    }

    /**
     * Method testCanParseDecimalWithSign.
     */
    @Test
    public void testCanParseDecimalWithSign() {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertTrue(stage.canParse("+80.909"));
    }

    /**
     * Method testCanParseEmptyString.
     */
    @Test
    public void testCanParseEmptyString() {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertFalse(stage.canParse(""));
    }

    /**
     * Method testParseWhiteSpaces.
     * @throws Exception
     */
    @Test
    public void testParseWhiteSpaces() throws Exception {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertFalse(stage.canParse("   "));
    }

    /**
     * Method testCanParseJustSign.
     * @throws Exception
     */
    @Test(expectedExceptions = InvalidRangeSyntaxException.class)
    public void testCanParseJustSign() throws Exception {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        stage.parse("+..50", new RangeConfig());
    }

    /**
     * Method testCanParseBadSign.
     * @throws Exception
     */
    @Test(expectedExceptions = RangeException.class)
    public void testCanParseBadSign() throws Exception {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        stage.parse("50+..50", new RangeConfig());
    }

    /**
     * Method testExpect.
     */
    @Test
    public void testExpect() {
        LeftBoundaryStage stage = new LeftBoundaryStage();
        assertEquals(stage.expects(), "+-[0-9]");
    }
}
