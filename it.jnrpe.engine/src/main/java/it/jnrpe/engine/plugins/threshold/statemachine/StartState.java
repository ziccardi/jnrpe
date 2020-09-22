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

class StartState extends AbstractState {
  @Override
  public boolean isValidToken(char c) {
    for (IParsingState state : this.getTransitions()) {
      if (state.isValidToken(c)) {
        return true;
      }
    }
    return false;
  }

  public IParsingState _parse(char c) throws ThresholdSyntaxError {
    for (IParsingState state : this.getTransitions()) {
      if (state.isValidToken(c)) {
        return state.parse(c);
      }
    }

    throw new ThresholdSyntaxError(c);
  }

  @Override
  public Optional<String> _expects() {
    return Optional.empty();
  }
}
