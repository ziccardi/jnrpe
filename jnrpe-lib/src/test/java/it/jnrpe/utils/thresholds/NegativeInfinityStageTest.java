package it.jnrpe.utils.thresholds;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class NegativeInfinityStageTest {
    @Test
    public void testCanParseNull() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertFalse(stage.canParse(null));
    }

    @Test
    public void testCanParseWithoutMinus() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertTrue(stage.canParse("inf"));
    }

    @Test
    public void testCanParseWithMinus() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertTrue(stage.canParse("-inf"));
    }

    @Test
    public void testExpect() {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertEquals(stage.expects(), "[-]inf");
    }

    @Test
    public void testParseWithSignOk() throws InvalidRangeSyntaxException {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertEquals(stage.parse("-inf..80", new RangeConfig()), "..80");
    }

    @Test
    public void testParseWithoutSignOk() throws InvalidRangeSyntaxException {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertEquals(stage.parse("inf..80", new RangeConfig()), "..80");
    }


    @Test
    public void testParseKo() throws InvalidRangeSyntaxException {
        NegativeInfinityStage stage = new NegativeInfinityStage();
        assertFalse(stage.canParse("50..80"));
    }
}
