package it.jnrpe.utils.thresholds;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class PositiveInfinityStageTest {
    @Test
    public void testCanParseNull() {
        PositiveInfinityStage stage = new PositiveInfinityStage();
        assertFalse(stage.canParse(null));
    }

    @Test
    public void testCanParseWithoutMinus() {
        PositiveInfinityStage stage = new PositiveInfinityStage();
        assertTrue(stage.canParse("inf"));
    }

    @Test
    public void testCanParseWithSign() {
        PositiveInfinityStage stage = new PositiveInfinityStage();
        assertTrue(stage.canParse("+inf"));
    }

    @Test
    public void testParseOk() throws InvalidRangeSyntaxException {
        PositiveInfinityStage stage = new PositiveInfinityStage();
        assertEquals(stage.parse("+inf..80", new RangeConfig()), "..80");
    }

    @Test
    public void testParseKo() throws InvalidRangeSyntaxException {
        PositiveInfinityStage stage = new PositiveInfinityStage();
        assertFalse(stage.canParse("10..80"));
    }

    @Test
    public void testExpect() {
        PositiveInfinityStage stage = new PositiveInfinityStage();
        assertEquals(stage.expects(), "[+]inf");
    }
}
