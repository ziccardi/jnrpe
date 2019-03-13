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

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.utils.BadThresholdException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;


/**
 */
public class ReturnValueBuilderTest {

    //metric={metric},ok={range},warn={range},crit={range},unit={unit}prefix={SI prefix}
    /**
     * Method testNoLevels.
     * @throws BadThresholdException
     */
    @Test
    public void testNoLevels() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC")
            .create();

        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
        	.withValue(
        	        Metric.forMetric("TEST_METRIC", Integer.class)
                            .withMessage("TEST_OK")
                            .withValue(10)
                            .withMinValue(0)
                            .withMaxValue(100)
                            .build()
        	        )
        	.create();
        
        Assert.assertEquals(Status.OK, ret.getStatus());
    }

    /**
     * Method testOnlyOKButCritical.
     * @throws BadThresholdException
     */
    @Test
    public void testOnlyOKButCritical() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(
                        Metric.forMetric("TEST_METRIC", Integer.class)
                                .withMessage("TEST_OK")
                                .withValue(10)
                                .withMinValue(0)
                                .withMaxValue(100)
                                .build()
                )
            	.create();

        Assert.assertEquals(Status.CRITICAL, ret.getStatus());
    }

    /**
     * Method testOnlyOK.
     * @throws BadThresholdException
     */
    @Test
    public void testOnlyOK() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(
                        Metric.forMetric("TEST_METRIC", Integer.class)
                                .withMessage("TEST_OK")
                                .withValue(51)
                                .withMinValue(0)
                                .withMaxValue(100)
                                .build()

                )
            	.create();

        Assert.assertEquals(Status.OK, ret.getStatus());
    }

    /**
     * Method testOkWarnCrit_ok.
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_ok() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300,unit=s")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(
                        Metric.forMetric("TEST_METRIC", Integer.class)
                                .withMessage("TEST_OK")
                                .withValue(60)
                                .withMinValue(0)
                                .withMaxValue(100)
                                .build()

                )
            	.create();

        Assert.assertEquals(Status.OK, ret.getStatus());
    }

    /**
     * Method testOkWarnCrit_warn.
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_warn() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(
                        Metric.forMetric("TEST_METRIC", Integer.class)
                                .withMessage("TEST_OK")
                                .withValue(110)
                                .withMinValue(0)
                                .withMaxValue(100)
                                .build()

                )
            	.create();

        Assert.assertEquals(Status.WARNING, ret.getStatus());
    }
    
    /**
     * Method testOkWarnCrit_crit.
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_crit() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(
                        Metric.forMetric("TEST_METRIC", Integer.class)
                                .withMessage("TEST_OK")
                                .withValue(210)
                                .withMinValue(0)
                                .withMaxValue(100)
                                .build()

                )
            	.create();

        Assert.assertEquals(Status.CRITICAL, ret.getStatus());
    }

    /**
     * Method testOkWarnCrit_okMega.
     * @throws BadThresholdException
     */
    @Test
    public void testOkWarnCrit_okMega() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300,unit=byte,prefix=mega")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
                .withValue(
                        Metric.forMetric("TEST_METRIC", Integer.class)
                        .withMessage("TEST OK")
                        .withValue(60)
                        .withMinValue(0)
                        .withMaxValue(100)
                        //.withPrefix(Prefixes.mega)
                        .build()
                        )
                .create();
        
        Assert.assertEquals(Status.OK, ret.getStatus());
    }
}
