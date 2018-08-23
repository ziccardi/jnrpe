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

import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricBuilder;
import it.jnrpe.utils.BadThresholdException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 */
public class ThresholdEvaluatorTest {

    private final static String METRIC_NAME = "TESTMETRIC";
    private final static String BAD_METRIC_NAME = "BAD_TESTMETRIC";

    private Metric buildMetric(int value) {
        return buildMetric(new BigDecimal(value), METRIC_NAME);
    }
    
    private Metric buildMetric(int value, Prefixes prefix) {
        return buildMetric(new BigDecimal(value), METRIC_NAME, prefix);
    }
    
    private Metric buildMetric(String value) {
        return buildMetric(new BigDecimal(value), METRIC_NAME);
    }

    private Metric buildMetric(int value, String metricName) {
        return buildMetric(new BigDecimal(value), metricName);
    }
    
    private Metric buildMetric(String value, String metricName) {
        return buildMetric(new BigDecimal(value), metricName);
    }

    private Metric buildMetric(BigDecimal value) {
        return buildMetric(value, METRIC_NAME);
    }

    private Metric buildMetric(BigDecimal value, String metricName) {
        return MetricBuilder.forMetric(metricName).withValue(value).build();
    }
    
    private Metric buildMetric(BigDecimal value, String metricName, Prefixes prefix) {
        return MetricBuilder.forMetric(metricName).withValue(value).withPrefix(prefix).build();
    }

    /**
     * Method testNoLevels.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testNoLevels() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+"").create();
        Status s = ths.evaluate(buildMetric("10"));
        Assert.assertEquals(Status.OK, s);
    }

    /**
     * Method testOnlyOKButCritical.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOnlyOKButCritical() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100").create();
        Status s = ths.evaluate(buildMetric("10"));
        Assert.assertEquals(s, Status.CRITICAL);
    }

    /**
     * Method testOnlyOK.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOnlyOK() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100").create();
        Status s = ths.evaluate(buildMetric("20"));
        Assert.assertEquals(Status.CRITICAL, s);
    }

    /**
     * Method testOkWarnCrit_ok.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_ok() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100,warn=100..200,crit=200..300").create();
        Status s = ths.evaluate(buildMetric("60"));
        Assert.assertEquals(Status.OK, s);
    }

    /**
     * Method testOkWarnCrit_warn.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_warn() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100,warn=100..200,crit=200..300").create();
        Status s = ths.evaluate(buildMetric("110"));
        Assert.assertEquals(Status.WARNING, s);
    }

    /**
     * Method testOkWarnCrit_warnMega.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_warnMega() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold(
                "metric="+METRIC_NAME+",ok=50..100,warn=100..200,crit=200..300,prefix=mega,unit=byte").create();
        Status s = ths.evaluate(buildMetric(110, Prefixes.mega));
        Assert.assertEquals(Status.WARNING, s);
    }

    /**
     * Method testOkWarnCrit_crit.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_crit() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100,warn=100..200,crit=200..300").create();
        Status s = ths.evaluate(buildMetric("210"));
        Assert.assertEquals(Status.CRITICAL, s);
    }

    /**
     * Method testNullMetricValue.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = NullPointerException.class)
    public void testNullMetricValue() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100,warn=100..200,crit=200..300").create();
        ths.evaluate(buildMetric((BigDecimal)null));
    }

    /**
     * Method testNullMetricName.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = NullPointerException.class)
    public void testNullMetricName() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=50..100,warn=100..200,crit=200..300").create();
        ths.evaluate(buildMetric(new BigDecimal("210"), null));
    }

    /**
     * Method testBadMetricPair.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = BadThresholdException.class)
    public void testBadMetricPair() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric=,ok=50..100,warn=100..200,crit=200..300").create();
        ths.evaluate(buildMetric(new BigDecimal("210"), null));
    }

    /**
     * Method testBadMetricPair2.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = BadThresholdException.class)
    public void testBadMetricPair2() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("=pippo,ok=50..100,warn=100..200,crit=200..300").create();
        ths.evaluate(buildMetric(new BigDecimal("210"), null));
    }

    /**
     * Method testBadOkPair.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = BadThresholdException.class)
    public void testBadOkPair() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=,warn=100..200,crit=200..300").create();
        ths.evaluate(buildMetric(new BigDecimal("210"), null));
    }

    /**
     * Method testBadWarnPair.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = BadThresholdException.class)
    public void testBadWarnPair() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=10..100,warn=,crit=200..300").create();
        ths.evaluate(buildMetric(new BigDecimal("210"), null));
    }

    /**
     * Method testBadCritPair.
     * 
     * @throws BadThresholdException
     */
    @Test(expected = BadThresholdException.class)
    public void testBadCritPair() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=10..100,warn=100..200,crit=").create();
        ths.evaluate(buildMetric(new BigDecimal("210"), null));
    }

    /**
     * Method testBadMetric.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testBadMetric() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withThreshold("metric="+METRIC_NAME+",ok=10..100,warn=100..200,crit=200..inf").create();
        Status s = ths.evaluate(buildMetric(new BigDecimal("210"), BAD_METRIC_NAME));
        Assert.assertEquals(Status.OK, s);
    }

}
