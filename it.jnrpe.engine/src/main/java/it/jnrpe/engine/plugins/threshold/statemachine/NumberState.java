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

import it.jnrpe.engine.plugins.threshold.ThresholdSyntaxError;
import java.math.BigDecimal;
import java.util.Optional;

class NumberState extends AbstractState {
  private boolean isMaxEdge = true;

  NumberState() {
    super();
  }

  @Override
  public boolean isValidToken(char c) {
    if (Character.isDigit(c) || (c == '.' && !this.getValue().contains("."))) {
      return true;
    }

    if (isEmpty()) {
      return false;
    }

    for (IParsingState state : getTransitions()) {
      if (state.isValidToken(c)) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected Optional<String> _expects() {
    return Optional.of("[0-9" + (this.getValue().contains(".") ? "]" : ".]"));
  }

  @Override
  protected IParsingState _parse(char c) throws ThresholdSyntaxError {
    if (isValidToken(c)) {
      if (Character.isDigit(c) || (c == '.' && !this.getValue().contains("."))) {
        appendToken(c);
        return this;
      }

      for (IParsingState state : getTransitions()) {
        if (state.isValidToken(c)) {
          return state.parse(c);
        }
      }
    }

    throw new ThresholdSyntaxError(c);
  }

  public void setAsMinEgde() {
    this.isMaxEdge = false;
  }

  public boolean isMaxEdge() {
    return this.isMaxEdge;
  }

  @Override
  public boolean isValidEndState() {
    return true;
  }

  @Override
  public void apply(ThresholdBuilder builder) {
    if (this.isMaxEdge) {
      builder.withMax(new BigDecimal(getValue()));
    } else {
      builder.withMin(new BigDecimal(getValue()));
    }
  }
}
