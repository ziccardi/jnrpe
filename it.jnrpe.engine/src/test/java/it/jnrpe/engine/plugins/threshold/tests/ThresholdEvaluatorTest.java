/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.engine.plugins.threshold.tests;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.plugins.threshold.ThresholdEvaluator;
import it.jnrpe.engine.plugins.threshold.ThresholdParser;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class ThresholdEvaluatorTest {
  @Test
  public void testPositiveSameValueNegativeMinAndMax() throws Exception {
    var evaluator =
        new ThresholdEvaluator()
            .withCriticalThreshold(ThresholdParser.parse("-20:-20"))
            .withWarningThreshold(ThresholdParser.parse("-10:-10"))
            .withOkThreshold(ThresholdParser.parse("-5:-5"));

    AtomicReference<String> result = new AtomicReference<>("UNKNOWN");

    evaluator
        .evaluate(new BigDecimal(5))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));

    assertEquals("CRITICAL", result.get());

    evaluator
        .evaluate(new BigDecimal(-5))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));

    assertEquals("OK", result.get());

    evaluator
        .evaluate(new BigDecimal(-10))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));
  }

  @Test
  public void testOverlappingRanges() throws Exception {
    var evaluator =
        new ThresholdEvaluator()
            .withCriticalThreshold(ThresholdParser.parse("~:-20"))
            .withWarningThreshold(ThresholdParser.parse("10:20"))
            .withOkThreshold(ThresholdParser.parse("-30:15"))
            .withOkThreshold(ThresholdParser.parse("50:"));

    AtomicReference<String> result = new AtomicReference<>("UNKNOWN");

    evaluator
        .evaluate(new BigDecimal(-100))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));
    assertEquals("CRITICAL", result.get());

    evaluator
        .evaluate(new BigDecimal(-25))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));
    assertEquals("OK", result.get());

    evaluator
        .evaluate(new BigDecimal(19))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));
    assertEquals("WARNING", result.get());

    evaluator
        .evaluate(new BigDecimal(25))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));
    assertEquals("CRITICAL", result.get());

    evaluator
        .evaluate(new BigDecimal(2000))
        .onCritical(() -> result.set("CRITICAL"))
        .onWarning(() -> result.set("WARNING"))
        .onOk(() -> result.set("OK"));
    assertEquals("OK", result.get());
  }
}
