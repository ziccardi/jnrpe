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
import java.util.Optional;

public abstract class AbstractUnaryState extends AbstractState {
  private final char token;

  AbstractUnaryState(char token) {
    this.token = token;
  }

  @Override
  public boolean isValidToken(char c) {
    if (isEmpty()) {
      return c == this.token;
    }

    for (IParsingState state : getTransitions()) {
      if (state.isValidToken(c)) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected IParsingState _parse(char c) throws ThresholdSyntaxError {
    if (isEmpty()) {
      if (isValidToken(c)) {
        appendToken(c);
        return this;
      }
      throw new IllegalStateException("Parse should not be called if `isValidToken` returns false");
    }

    for (IParsingState state : getTransitions()) {
      if (state.isValidToken(c)) {
        return state.parse(c);
      }
    }

    throw new ThresholdSyntaxError(c);
  }

  @Override
  protected Optional<String> _expects() {
    return Optional.ofNullable(hasValue() ? null : String.valueOf(this.token));
  }
}
