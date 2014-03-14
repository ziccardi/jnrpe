package it.jnrpe.utils.thresholds;

import static org.testng.Assert.assertEquals;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.utils.BadThresholdException;

import java.math.BigDecimal;

import org.testng.annotations.Test;

public class ReturnValueBuilderTest {

//    //metric={metric},ok={range},warn={range},crit={range},unit={unit}prefix={SI prefix}
//    @Test
//    public void testNoLevels() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo")
//            .create();
//
//        ReturnValue ret = new ReturnValueBuilder(ths)
//            .withMessage("TEST OK")
//            .withValue("pippo", new BigDecimal(10), new BigDecimal(0), new BigDecimal(100))
//            .create();
//
//        assertEquals(ret.getStatus(), Status.OK);
//    }
//
//    @Test
//    public void testOnlyOKButCritical() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo,ok=50..100")
//            .create();
//        ReturnValue ret = new ReturnValueBuilder(ths)
//        .withMessage("TEST OK")
//        .withValue("pippo", new BigDecimal(10), new BigDecimal(0), new BigDecimal(100))
//        .create();
//
//        assertEquals(ret.getStatus(), Status.CRITICAL);
//    }
//
//    @Test
//    public void testOnlyOK() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo,ok=50..100")
//            .create();
//        ReturnValue ret = new ReturnValueBuilder(ths)
//        .withMessage("TEST OK")
//        .withValue("pippo", new BigDecimal(10), new BigDecimal(0), new BigDecimal(100))
//        .create();
//
//        assertEquals(ret.getStatus(), Status.CRITICAL);
//    }
//
//    @Test
//    public void testOkWarnCrit_ok() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo,ok=50..100,warn=100..200,crit=200..300,unit=s")
//            .create();
//        ReturnValue ret = new ReturnValueBuilder(ths)
//        .withMessage("TEST OK")
//        .withValue("pippo", new BigDecimal(60), new BigDecimal(0), new BigDecimal(100))
//        .create();
//        assertEquals(ret.getStatus(), Status.OK);
//    }
//
//    @Test
//    public void testOkWarnCrit_warn() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo,ok=50..100,warn=100..200,crit=200..300")
//            .create();
//        ReturnValue ret = new ReturnValueBuilder(ths)
//        .withMessage("TEST OK")
//        .withValue("pippo", new BigDecimal(110), new BigDecimal(0), new BigDecimal(100))
//        .create();
//        assertEquals(ret.getStatus(), Status.WARNING);
//    }
////
//    @Test
//    public void testOkWarnCrit_crit() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo,ok=50..100,warn=100..200,crit=200..300")
//            .create();
//        ReturnValue ret = new ReturnValueBuilder(ths)
//        .withMessage("TEST OK")
//        .withValue("pippo", new BigDecimal(210), new BigDecimal(0), new BigDecimal(100))
//        .create();
//        assertEquals(ret.getStatus(), Status.CRITICAL);
//    }
//
//    @Test
//    public void testOkWarnCrit_okMega() throws BadThresholdException {
//        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
//            .withThreshold("metric=pippo,ok=50..100,warn=100..200,crit=200..300,unit=byte,prefix=mega")
//            .create();
//        ReturnValue ret = new ReturnValueBuilder(ths)
//        .withMessage("TEST OK")
//        .withValue("pippo", Prefixes.mega.convert(60), new BigDecimal(0), new BigDecimal(100))
//        .create();
//        assertEquals(ret.getStatus(), Status.OK);
//    }
}
