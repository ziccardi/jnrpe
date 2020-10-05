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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ThresholdEvaluator {
  private final Set<IThreshold> criticalThresholds = new HashSet<>();
  private final Set<IThreshold> warningThresholds = new HashSet<>();
  private final Set<IThreshold> okThresholds = new HashSet<>();

  public class ThresholdRunnersExecutor {
    private final BigDecimal value;

    private ThresholdRunnersExecutor(BigDecimal value) {
      this.value = value;
    }

    public ThresholdRunnersExecutor onCritical(Runnable onCritical) {
      if (isCritical()) {
        onCritical.run();
      }
      return ThresholdRunnersExecutor.this;
    }

    public ThresholdRunnersExecutor onWarning(Runnable onWarning) {
      if (isWarning()) {
        onWarning.run();
      }
      return ThresholdRunnersExecutor.this;
    }

    public ThresholdRunnersExecutor onOk(Runnable onOk) {
      if (isOk()) {
        onOk.run();
      }
      return ThresholdRunnersExecutor.this;
    }

    public boolean isCritical() {
      return ThresholdEvaluator.this.isCritical(this.value);
    }

    public boolean isWarning() {
      return ThresholdEvaluator.this.isWarning(this.value);
    }

    public boolean isOk() {
      return ThresholdEvaluator.this.isOk(this.value);
    }
  }

  private boolean isOk(BigDecimal value) {
    if (criticalThresholds.isEmpty() && warningThresholds.isEmpty() && okThresholds.isEmpty()) {
      return true;
    }
    for (var thr : okThresholds) {
      if (thr.fallsInside(value)) {
        return true;
      }
    }

    for (var thr : criticalThresholds) {
      if (thr.fallsInside(value)) {
        return false;
      }
    }

    for (var thr : warningThresholds) {
      if (thr.fallsInside(value)) {
        return false;
      }
    }

    return okThresholds.isEmpty();
  }

  private boolean isCritical(BigDecimal value) {
    if (isOk(value)) {
      return false;
    }

    for (var thr : criticalThresholds) {
      if (thr.fallsInside(value)) {
        return true;
      }
    }

    for (var thr : warningThresholds) {
      if (thr.fallsInside(value)) {
        return false;
      }
    }

    return !okThresholds.isEmpty();
  }

  private boolean isWarning(BigDecimal value) {
    if (isOk(value) || isCritical(value)) {
      return false;
    }

    for (var thr : criticalThresholds) {
      if (thr.fallsInside(value)) {
        return true;
      }
    }

    return !okThresholds.isEmpty();
  }

  public ThresholdEvaluator withCriticalThreshold(IThreshold thr) {
    Objects.requireNonNull(thr);
    this.criticalThresholds.add(thr);
    return this;
  }

  public ThresholdEvaluator withWarningThreshold(IThreshold thr) {
    Objects.requireNonNull(thr);
    this.warningThresholds.add(thr);
    return this;
  }

  public ThresholdEvaluator withOkThreshold(IThreshold thr) {
    Objects.requireNonNull(thr);
    this.okThresholds.add(thr);
    return this;
  }

  public ThresholdRunnersExecutor evaluate(BigDecimal value) {
    Objects.requireNonNull(value);
    return new ThresholdRunnersExecutor(value);
  }
}
