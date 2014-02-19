package it.jnrpe.utils.thresholds;

import static org.testng.Assert.assertEquals;
import it.jnrpe.Status;
import it.jnrpe.utils.BadThresholdException;

import java.math.BigDecimal;

import org.testng.annotations.Test;

public class LegacyThresholdEvaluatorTest {

    //metric={metric},ok={range},warn={range},crit={range},unit={unit}prefix={SI prefix}
    @Test
    public void testNoLevels() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", null, null, null)
            .create();
        Status s = ths.evaluate("pippo", new BigDecimal("10"));
        assertEquals(s, Status.OK);
    }

    @Test
    public void testOnlyOKButCritical() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
        .withLegacyThreshold("pippo", "10:20", null, null)
            .create();
        Status s = ths.evaluate("pippo", new BigDecimal("30"));
        assertEquals(s, Status.CRITICAL);
    }

    @Test
    public void testOkWarnCrit_ok() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", "50:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate("pippo", new BigDecimal("60"));
        assertEquals(s, Status.OK);
    }

    @Test
    public void testOkWarnCrit_warn() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", "50:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate("pippo", new BigDecimal("110"));
        assertEquals(s, Status.WARNING);
    }

    @Test
    public void testOkWarnCrit_crit() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
        .withLegacyThreshold("pippo", "50:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate("pippo", new BigDecimal("210"));
        assertEquals(s, Status.CRITICAL);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testNullMetricValue() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", "50:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate("pippo", null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testNullMetricName() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", "50:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate(null, new BigDecimal("210"));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testBadMetricPair() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", "50.:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate(null, new BigDecimal("210"));
    }

    @Test(expectedExceptions=BadThresholdException.class)
    public void testBadMetric() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withLegacyThreshold("pippo", "50..:100", "100:200","200:300")
            .create();
        Status s = ths.evaluate("pluto", new BigDecimal("210"));
        assertEquals(s, Status.OK);
    }

}
