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
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricBuilder;
import it.jnrpe.utils.BadThresholdException;

import java.math.BigDecimal;

import org.testng.annotations.Test;

/**
 */
public class LegacyThresholdEvaluatorTest {

    // metric={metric},ok={range},warn={range},crit={range},unit={unit}prefix={SI
    // prefix}

    private final static String METRIC_NAME = "TESTMETRIC";
    private final static String BAD_METRIC_NAME = "BAD_TESTMETRIC";
    
    private Metric buildMetric(int value) {
        return buildMetric(new BigDecimal(value), METRIC_NAME);
    }
    
    private Metric buildMetric(int value, String metricName) {
        return buildMetric(new BigDecimal(value), metricName);
    }
    
    private Metric buildMetric(BigDecimal value) {
        return buildMetric(value, METRIC_NAME);
    }
    
    private Metric buildMetric(BigDecimal value, String metricName) {
        return MetricBuilder.forMetric(metricName)
                .withValue(value).build();
    }
    
    /**
     * Method testNoLevels.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testNoLevels() throws BadThresholdException {

        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, null, null, null).create();
        Status s = ths.evaluate(buildMetric(10));
        assertEquals(s, Status.OK);
    }

    /**
     * Method testOnlyOKButCritical.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOnlyOKButCritical() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "10:20", null, null).create();
        Status s = ths.evaluate(buildMetric(30));
        assertEquals(s, Status.CRITICAL);
    }

    /**
     * Method testOkWarnCrit_ok.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_ok() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50:100", "100:200", "200:300").create();
        Status s = ths.evaluate(buildMetric(60));
        assertEquals(s, Status.OK);
    }

    /**
     * Method testOkWarnCrit_warn.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_warn() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50:100", "100:200", "200:300").create();
        Status s = ths.evaluate(buildMetric(110));
        assertEquals(s, Status.WARNING);
    }

    /**
     * Method testOkWarnCrit_crit.
     * 
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_crit() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50:100", "100:200", "200:300").create();
        Status s = ths.evaluate(buildMetric(210));
        assertEquals(s, Status.CRITICAL);
    }

    /**
     * Method testNullMetricValue.
     * 
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testNullMetricValue() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50:100", "100:200", "200:300").create();
        ths.evaluate(buildMetric(null));
    }

    /**
     * Method testNullMetricName.
     * 
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testNullMetricName() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50:100", "100:200", "200:300").create();
        ths.evaluate(null);
    }

    /**
     * Method testBadMetricPair.
     * 
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testBadMetricPair() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50.:100", "100:200", "200:300").create();
        ths.evaluate(null);
    }

    /**
     * Method testBadMetric.
     * 
     * @throws BadThresholdException
     */
    @Test(expectedExceptions = BadThresholdException.class)
    public void testBadMetric() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder().withLegacyThreshold(METRIC_NAME, "50..:100", "100:200", "200:300").create();
        Status s = ths.evaluate(buildMetric(210, BAD_METRIC_NAME));
        assertEquals(s, Status.OK);
    }

}
