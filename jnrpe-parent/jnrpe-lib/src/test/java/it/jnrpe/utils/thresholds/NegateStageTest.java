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
