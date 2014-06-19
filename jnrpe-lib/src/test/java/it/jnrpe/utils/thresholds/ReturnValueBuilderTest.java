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
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.utils.BadThresholdException;

import java.math.BigDecimal;

import org.testng.annotations.Test;

public class ReturnValueBuilderTest {

    //metric={metric},ok={range},warn={range},crit={range},unit={unit}prefix={SI prefix}
    @Test
    public void testNoLevels() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC")
            .create();

        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
        	.withValue(new Metric("TEST_METRIC", "TEST OK", new BigDecimal(10), new BigDecimal(0), new BigDecimal(100)))
        	.create();
        
        assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void testOnlyOKButCritical() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(new Metric("TEST_METRIC", "TEST OK", new BigDecimal(10), new BigDecimal(0), new BigDecimal(100)))
            	.create();
        
        assertEquals(ret.getStatus(), Status.CRITICAL);
    }

    @Test
    public void testOnlyOK() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(new Metric("TEST_METRIC", "TEST OK", new BigDecimal(51), new BigDecimal(0), new BigDecimal(100)))
            	.create();
        
        assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void testOkWarnCrit_ok() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300,unit=s")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(new Metric("TEST_METRIC", "TEST OK", new BigDecimal(60), new BigDecimal(0), new BigDecimal(100)))
            	.create();
        
        assertEquals(ret.getStatus(), Status.OK);
    }

    @Test
    public void testOkWarnCrit_warn() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(new Metric("TEST_METRIC", "TEST OK", new BigDecimal(110), new BigDecimal(0), new BigDecimal(100)))
            	.create();
        
        assertEquals(ret.getStatus(), Status.WARNING);
    }
    
    @Test
    public void testOkWarnCrit_crit() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(new Metric("TEST_METRIC", "TEST OK", new BigDecimal(210), new BigDecimal(0), new BigDecimal(100)))
            	.create();
        
        assertEquals(ret.getStatus(), Status.CRITICAL);
    }

    @Test
    public void testOkWarnCrit_okMega() throws BadThresholdException {
        ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
            .withThreshold("metric=TEST_METRIC,ok=50..100,warn=100..200,crit=200..300,unit=byte,prefix=mega")
            .create();
        
        ReturnValue ret = ReturnValueBuilder.forPlugin("TEST_PLUGIN", ths)
            	.withValue(new Metric("TEST_METRIC", "TEST OK", Prefixes.mega.convert(60), new BigDecimal(0), new BigDecimal(100)))
            	.create();
        
        assertEquals(ret.getStatus(), Status.OK);
    }
}
