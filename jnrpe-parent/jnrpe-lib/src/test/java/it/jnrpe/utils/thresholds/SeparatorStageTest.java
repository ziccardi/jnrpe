package it.jnrpe.utils.thresholds;

import org.testng.annotations.Test;

import it.jnrpe.utils.thresholds.BracketStage.ClosedBracketStage;
import it.jnrpe.utils.thresholds.BracketStage.OpenBracketStage;
import static org.testng.Assert.*;

public class SeparatorStageTest {

    @Test
    public void testParseOk() {
        SeparatorStage stage = new SeparatorStage();

        assertEquals(stage.parse("..10", new RangeConfig()), "10");
    }

    @Test
    public void testParseKo() {
        SeparatorStage stage = new SeparatorStage();
        assertFalse(stage.canParse("a"));
    }

}
