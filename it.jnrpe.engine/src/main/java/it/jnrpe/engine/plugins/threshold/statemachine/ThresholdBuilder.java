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

import it.jnrpe.engine.plugins.threshold.IThreshold;
import java.math.BigDecimal;

public class ThresholdBuilder {
  private boolean negate = false;
  private boolean infinity = false;
  private boolean negativeInfinity = false;
  private boolean minus = false;
  private BigDecimal minValue = null;
  private BigDecimal maxValue = null;

  ThresholdBuilder negate() {
    this.negate = true;
    return this;
  }

  ThresholdBuilder negativeInfinity() {
    this.negativeInfinity = true;
    return this;
  }

  ThresholdBuilder infinity() {
    this.infinity = true;
    return this;
  }

  ThresholdBuilder minus() {
    this.minus = true;
    return this;
  }

  ThresholdBuilder withMin(BigDecimal min) {
    this.minValue = this.minus ? min.negate() : min;
    this.minus = false;
    return this;
  }

  ThresholdBuilder withMax(BigDecimal max) {
    this.maxValue = this.minus ? max.negate() : max;
    this.minus = false;
    this.infinity = false;
    return this;
  }

  public IThreshold build() {
    ThresholdImpl thr = new ThresholdImpl();
    if (this.negate) {
      thr.negate();
    }
    if (this.negativeInfinity) {
      thr.setMin(ThresholdEdge.NEGATIVE_INFINITY);
    }
    if (this.minValue != null) {
      thr.setMin(new ThresholdEdge(this.minValue));
    }
    if (this.infinity) {
      thr.setMax(ThresholdEdge.INFINITY);
    }
    if (this.maxValue != null) {
      thr.setMax(new ThresholdEdge(this.maxValue));
    }

    return thr;
  }

  @Override
  public String toString() {
    return "ThresholdBuilder{"
        + "negate="
        + negate
        + ", infinity="
        + infinity
        + ", negativeInfinity="
        + negativeInfinity
        + ", minus="
        + minus
        + ", minValue="
        + minValue
        + ", maxValue="
        + maxValue
        + '}';
  }
}
