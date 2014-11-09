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
import it.jnrpe.utils.BadThresholdException;

import java.math.BigDecimal;

import org.testng.annotations.Test;

/**
 */
public class ThresholdEvaluatorTest {

	// metric={metric},ok={range},warn={range},crit={range},unit={unit}prefix={SI
	// prefix}
	/**
	 * Method testNoLevels.
	 * @throws BadThresholdException
	 */
	@Test
	public void testNoLevels() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=pippo").create();
		Status s = ths.evaluate("pippo", new BigDecimal("10"));
		assertEquals(s, Status.OK);
	}

	/**
	 * Method testOnlyOKButCritical.
	 * @throws BadThresholdException
	 */
	@Test
	public void testOnlyOKButCritical() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=pippo,ok=50..100").create();
		Status s = ths.evaluate("pippo", new BigDecimal("10"));
		assertEquals(s, Status.CRITICAL);
	}

	/**
	 * Method testOnlyOK.
	 * @throws BadThresholdException
	 */
	@Test
	public void testOnlyOK() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=pippo,ok=50..100").create();
		Status s = ths.evaluate("pippo", new BigDecimal("20"));
		assertEquals(s, Status.CRITICAL);
	}

	/**
	 * Method testOkWarnCrit_ok.
	 * @throws BadThresholdException
	 */
	@Test
	public void testOkWarnCrit_ok() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=50..100,warn=100..200,crit=200..300")
				.create();
		Status s = ths.evaluate("pippo", new BigDecimal("60"));
		assertEquals(s, Status.OK);
	}

	/**
	 * Method testOkWarnCrit_warn.
	 * @throws BadThresholdException
	 */
	@Test
	public void testOkWarnCrit_warn() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=50..100,warn=100..200,crit=200..300")
				.create();
		Status s = ths.evaluate("pippo", new BigDecimal("110"));
		assertEquals(s, Status.WARNING);
	}

	/**
	 * Method testOkWarnCrit_warnMega.
	 * @throws BadThresholdException
	 */
	@Test
	public void testOkWarnCrit_warnMega() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=50..100,warn=100..200,crit=200..300,prefix=mega,unit=byte")
				.create();
		Status s = ths.evaluate("pippo", Prefixes.mega.convert(110));
		assertEquals(s, Status.WARNING);
	}

	/**
	 * Method testOkWarnCrit_crit.
	 * @throws BadThresholdException
	 */
	@Test
	public void testOkWarnCrit_crit() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=50..100,warn=100..200,crit=200..300")
				.create();
		Status s = ths.evaluate("pippo", new BigDecimal("210"));
		assertEquals(s, Status.CRITICAL);
	}

	/**
	 * Method testNullMetricValue.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testNullMetricValue() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=50..100,warn=100..200,crit=200..300")
				.create();
		ths.evaluate("pippo", null);
	}

	/**
	 * Method testNullMetricName.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testNullMetricName() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=50..100,warn=100..200,crit=200..300")
				.create();
		ths.evaluate(null, new BigDecimal("210"));
	}

	/**
	 * Method testBadMetricPair.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = BadThresholdException.class)
	public void testBadMetricPair() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=,ok=50..100,warn=100..200,crit=200..300")
				.create();
		ths.evaluate(null, new BigDecimal("210"));
	}

	/**
	 * Method testBadMetricPair2.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = BadThresholdException.class)
	public void testBadMetricPair2() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("=pippo,ok=50..100,warn=100..200,crit=200..300")
				.create();
		ths.evaluate(null, new BigDecimal("210"));
	}

	/**
	 * Method testBadOkPair.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = BadThresholdException.class)
	public void testBadOkPair() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=pippo,ok=,warn=100..200,crit=200..300")
				.create();
		ths.evaluate(null, new BigDecimal("210"));
	}

	/**
	 * Method testBadWarnPair.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = BadThresholdException.class)
	public void testBadWarnPair() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=pippo,ok=10..100,warn=,crit=200..300")
				.create();
		ths.evaluate(null, new BigDecimal("210"));
	}

	/**
	 * Method testBadCritPair.
	 * @throws BadThresholdException
	 */
	@Test(expectedExceptions = BadThresholdException.class)
	public void testBadCritPair() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold("metric=pippo,ok=10..100,warn=100..200,crit=")
				.create();
		ths.evaluate(null, new BigDecimal("210"));
	}

	/**
	 * Method testBadMetric.
	 * @throws BadThresholdException
	 */
	@Test
	public void testBadMetric() throws BadThresholdException {
		ThresholdsEvaluator ths = new ThresholdsEvaluatorBuilder()
				.withThreshold(
						"metric=pippo,ok=10..100,warn=100..200,crit=200..inf")
				.create();
		Status s = ths.evaluate("pluto", new BigDecimal("210"));
		assertEquals(s, Status.OK);
	}

}
