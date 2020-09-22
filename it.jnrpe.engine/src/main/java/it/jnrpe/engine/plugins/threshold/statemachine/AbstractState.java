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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class AbstractState implements IParsingState {
  private final List<IParsingState> transitions = new ArrayList<>();
  private StringBuffer value = new StringBuffer();

  @Override
  public IParsingState addTransition(IParsingState state) {
    transitions.add(state);
    return this;
  }

  protected List<IParsingState> getTransitions() {
    return transitions;
  }

  @Override
  public final String expects(int depth) {
    List<String> expected = new ArrayList<>();
    _expects().ifPresent(expected::add);

    if (depth != 0) {
      transitions.forEach(state -> expected.add(state.expects(depth - 1)));
    }

    return String.join(",", expected);
  }

  public final IParsingState parse(char c) throws ThresholdSyntaxError {
    return _parse(c);
  }

  protected abstract IParsingState _parse(char c) throws ThresholdSyntaxError;

  protected abstract Optional<String> _expects();

  protected void appendToken(char c) {
    this.value.append(c);
  }

  @Override
  public String getValue() {
    return value.toString();
  }

  public boolean hasValue() {
    return this.value.length() > 0;
  }

  public boolean isEmpty() {
    return this.value.length() == 0;
  }
}
