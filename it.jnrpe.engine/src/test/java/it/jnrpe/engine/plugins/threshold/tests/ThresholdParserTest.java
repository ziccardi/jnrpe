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
package it.jnrpe.engine.plugins.threshold;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Threshold Parser")
class ThresholdParserTest {

  @Test
  public void testPositiveSameValueNegativeMinAndMax() throws Exception {
    var thr = ThresholdParser.parse("-20:-20");
    assertTrue(thr.fallsInside(new BigDecimal(-20)));
    assertFalse(thr.fallsInside(new BigDecimal(-21)));
    assertFalse(thr.fallsInside(new BigDecimal(-19)));
    assertFalse(thr.fallsInside(new BigDecimal(5)));
    assertFalse(thr.fallsInside(new BigDecimal(21)));
  }

  @Test
  public void testPositiveNegativeMinAndPositiveMax() throws Exception {
    var thr = ThresholdParser.parse("-10:20");
    assertTrue(thr.fallsInside(new BigDecimal(19)));
    assertTrue(thr.fallsInside(new BigDecimal(20)));
    assertTrue(thr.fallsInside(new BigDecimal(0)));
    assertTrue(thr.fallsInside(new BigDecimal(-10)));
    assertFalse(thr.fallsInside(new BigDecimal(-11)));
    assertFalse(thr.fallsInside(new BigDecimal(21)));
  }

  @Test
  public void testPositiveMinAndMax() throws Exception {
    var thr = ThresholdParser.parse("10:20");
    assertTrue(thr.fallsInside(new BigDecimal(10)));
    assertTrue(thr.fallsInside(new BigDecimal(20)));
    assertFalse(thr.fallsInside(new BigDecimal(9)));
    assertFalse(thr.fallsInside(new BigDecimal(0)));
  }

  @Test
  public void testInfinityMax() throws Exception {
    var thr = ThresholdParser.parse("10:");
    assertTrue(thr.fallsInside(new BigDecimal(10)));
    assertTrue(thr.fallsInside(new BigDecimal(11)));
    assertFalse(thr.fallsInside(new BigDecimal(-1)));
    assertFalse(thr.fallsInside(new BigDecimal(0)));
    assertFalse(thr.fallsInside(new BigDecimal(9)));
  }

  @Test
  public void testDecimalMinAndMax() throws Exception {
    var thr = ThresholdParser.parse("-10.5:20.9");
    assertFalse(thr.fallsInside(new BigDecimal(-20)));
    assertTrue(thr.fallsInside(new BigDecimal(-1)));
    assertTrue(thr.fallsInside(new BigDecimal(0)));
    assertTrue(thr.fallsInside(new BigDecimal(5)));
    assertTrue(thr.fallsInside(new BigDecimal(10)));
    assertTrue(thr.fallsInside(new BigDecimal("20.9")));
    assertFalse(thr.fallsInside(new BigDecimal("20.91")));
  }

  @Test
  public void testNoMinPositiveMax() throws Exception {
    var thr = ThresholdParser.parse(":10");
    assertFalse(thr.fallsInside(new BigDecimal(-20)));
    assertFalse(thr.fallsInside(new BigDecimal(-1)));
    assertTrue(thr.fallsInside(new BigDecimal(0)));
    assertTrue(thr.fallsInside(new BigDecimal(5)));
    assertTrue(thr.fallsInside(new BigDecimal(10)));
    assertFalse(thr.fallsInside(new BigDecimal(11)));
  }

  @Test
  public void testNoMinNoColon() throws Exception {
    var thr = ThresholdParser.parse("1121");
    assertTrue(thr.fallsInside(new BigDecimal(10)));
    assertFalse(thr.fallsInside(new BigDecimal(1122)));
    assertFalse(thr.fallsInside(new BigDecimal(-1)));
    assertTrue(thr.fallsInside(new BigDecimal(1121)));
  }

  @Test
  public void testNegativeInfinityWithNegativeMax() throws Exception {
    var thr = ThresholdParser.parse("~:-20");
    assertTrue(thr.fallsInside(new BigDecimal(-20)));
    assertTrue(thr.fallsInside(new BigDecimal(-21)));
    assertFalse(thr.fallsInside(new BigDecimal(-19)));
    assertFalse(thr.fallsInside(new BigDecimal(5)));
    assertFalse(thr.fallsInside(new BigDecimal(21)));
  }

  @Test
  public void testNegativeInfinityWithPositiveMax() throws Exception {
    var thr = ThresholdParser.parse("~:1121");
    assertTrue(thr.fallsInside(new BigDecimal(10)));
    assertFalse(thr.fallsInside(new BigDecimal(1122)));
    assertTrue(thr.fallsInside(new BigDecimal(-1)));
    assertTrue(thr.fallsInside(new BigDecimal(1121)));
    assertTrue(thr.fallsInside(new BigDecimal("-384568345879984")));
  }

  // Negate tests
  @Test
  public void testNegateWithNegativeInfinity() throws Exception {
    var thr = ThresholdParser.parse("@~:20");
    assertFalse(thr.fallsInside(new BigDecimal(19)));
    assertFalse(thr.fallsInside(new BigDecimal(20)));
    assertTrue(thr.fallsInside(new BigDecimal(21)));
    assertTrue(thr.fallsInside(new BigDecimal("2395789347593")));
    assertFalse(thr.fallsInside(new BigDecimal("-2395789347593")));
  }

  @Test
  public void testNegateWithNegativeInfinityAndNegativeMax() throws Exception {
    var thr = ThresholdParser.parse("@~:-20");
    assertFalse(thr.fallsInside(new BigDecimal(-20)));
    assertFalse(thr.fallsInside(new BigDecimal(-21)));
    assertTrue(thr.fallsInside(new BigDecimal(-19)));
    assertTrue(thr.fallsInside(new BigDecimal(5)));
    assertTrue(thr.fallsInside(new BigDecimal(21)));
  }

  @Test
  public void testNegateWithInfinityMax() throws Exception {
    var thr = ThresholdParser.parse("@10:");
    assertTrue(thr.fallsInside(new BigDecimal(-20)));
    assertTrue(thr.fallsInside(new BigDecimal(-1)));
    assertTrue(thr.fallsInside(new BigDecimal(0)));
    assertTrue(thr.fallsInside(new BigDecimal(5)));
    assertFalse(thr.fallsInside(new BigDecimal(10)));
    assertFalse(thr.fallsInside(new BigDecimal(11)));
  }

  @Test
  public void testNegateWithNegativeMinAndPositiveMax() throws Exception {
    var thr = ThresholdParser.parse("@-10:20");
    assertFalse(thr.fallsInside(new BigDecimal(19)));
    assertFalse(thr.fallsInside(new BigDecimal(20)));
    assertFalse(thr.fallsInside(new BigDecimal(0)));
    assertFalse(thr.fallsInside(new BigDecimal(-10)));
    assertTrue(thr.fallsInside(new BigDecimal(-11)));
    assertTrue(thr.fallsInside(new BigDecimal(21)));
  }

  @Test
  public void testNegateWithSameMinAndMax() throws Exception {
    var thr = ThresholdParser.parse("@-20:-20");
    assertFalse(thr.fallsInside(new BigDecimal(-20)));
    assertTrue(thr.fallsInside(new BigDecimal(-21)));
    assertTrue(thr.fallsInside(new BigDecimal(-19)));
    assertTrue(thr.fallsInside(new BigDecimal(5)));
    assertTrue(thr.fallsInside(new BigDecimal(21)));
  }

  @Test
  public void testNegateWithMinAndMax() throws Exception {
    var thr = ThresholdParser.parse("@10:20");
    assertFalse(thr.fallsInside(new BigDecimal(10)));
    assertFalse(thr.fallsInside(new BigDecimal(20)));
    assertTrue(thr.fallsInside(new BigDecimal(9)));
    assertTrue(thr.fallsInside(new BigDecimal(0)));
  }

  @Test
  public void testNegateWithNoMin() throws Exception {
    var thr = ThresholdParser.parse("@:1121");
    assertFalse(thr.fallsInside(new BigDecimal(10)));
    assertTrue(thr.fallsInside(new BigDecimal(1122)));
    assertTrue(thr.fallsInside(new BigDecimal(-1)));
    assertFalse(thr.fallsInside(new BigDecimal(1121)));
  }

  @Test
  public void testNegateWithNoMax() throws Exception {
    var thr = ThresholdParser.parse("@1121");
    assertFalse(thr.fallsInside(new BigDecimal(10)));
    assertTrue(thr.fallsInside(new BigDecimal(1122)));
    assertTrue(thr.fallsInside(new BigDecimal(-1)));
    assertFalse(thr.fallsInside(new BigDecimal(1121)));
  }
}
