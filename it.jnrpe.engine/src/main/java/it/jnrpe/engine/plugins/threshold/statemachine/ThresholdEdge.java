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
package it.jnrpe.engine.plugins.threshold.statemachine;

import java.math.BigDecimal;
import java.util.Objects;

class ThresholdEdge {
  private final boolean infinity;
  private final boolean negativeInfinity;
  private final BigDecimal value;

  private ThresholdEdge(boolean infinity, boolean negativeInfinity) {
    this.infinity = infinity;
    this.negativeInfinity = negativeInfinity;
    this.value = null;
  }

  public ThresholdEdge(BigDecimal value) {
    this.infinity = false;
    this.negativeInfinity = false;
    this.value = value;
  }

  public static ThresholdEdge NEGATIVE_INFINITY = new ThresholdEdge(false, true);
  public static ThresholdEdge INFINITY = new ThresholdEdge(true, false);

  public boolean greaterThan(BigDecimal value) {
    Objects.requireNonNull(value, "A value must be specified when greaterThan is invoked");
    if (this.infinity) {
      return true;
    }
    if (this.negativeInfinity) {
      return false;
    }
    Objects.requireNonNull(
        this.value,
        "A value must be specified when greaterThan is invoked and value is not infinity");
    return this.value.compareTo(value) > 0;
  }

  public boolean greaterThanOrEqual(BigDecimal value) {
    Objects.requireNonNull(value, "A value must be specified when greaterThanOrEqual is invoked");
    if (this.infinity) {
      return true;
    }
    if (this.negativeInfinity) {
      return false;
    }
    Objects.requireNonNull(
        this.value,
        "A value must be specified when greaterThanOrEqual is invoked and value is not infinity");
    return this.value.compareTo(value) >= 0;
  }

  public boolean lessThan(BigDecimal value) {
    Objects.requireNonNull(value, "A value must be specified when lessThan is invoked");
    if (this.negativeInfinity) {
      return true;
    }
    if (this.infinity) {
      return false;
    }
    Objects.requireNonNull(
        this.value,
        "A value must be specified when lessThan is invoked and value is not negativeInfinity");
    return this.value.compareTo(value) < 0;
  }

  public boolean lessThanOrEqual(BigDecimal value) {
    Objects.requireNonNull(value, "A value must be specified when lessOrEqualThan is invoked");
    if (this.negativeInfinity) {
      return true;
    }
    if (this.infinity) {
      return false;
    }
    Objects.requireNonNull(
        this.value,
        "A value must be specified when lessThanOrEqual is invoked and value is not negativeInfinity");
    return this.value.compareTo(value) <= 0;
  }

  @Override
  public String toString() {
    return "ThresholdEdge{"
        + "infinity="
        + infinity
        + ", negativeInfinity="
        + negativeInfinity
        + ", value="
        + value
        + '}';
  }
}
