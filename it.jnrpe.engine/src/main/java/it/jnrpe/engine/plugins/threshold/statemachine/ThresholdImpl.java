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

class ThresholdImpl implements IThreshold {
  private boolean negate;
  private ThresholdEdge min = new ThresholdEdge(new BigDecimal(0));
  private ThresholdEdge max;

  void setMin(ThresholdEdge min) {
    this.min = min;
  }

  void setMax(ThresholdEdge max) {
    this.max = max;
  }

  void negate() {
    this.negate = true;
  }

  public boolean fallsInside(BigDecimal value) {
    var res = min.lessThanOrEqual(value) && max.greaterThanOrEqual(value);
    return negate != res;
  }

  @Override
  public String toString() {
    return "ThresholdImpl{" + "negate=" + negate + ", min=" + min + ", max=" + max + '}';
  }
}
