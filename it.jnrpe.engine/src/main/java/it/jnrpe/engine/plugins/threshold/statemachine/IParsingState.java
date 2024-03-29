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

public interface IParsingState {
  default boolean isValidEndState() {
    return false;
  }

  boolean isValidToken(char c);

  IParsingState parse(char c) throws ThresholdSyntaxError;

  IParsingState addTransition(IParsingState state);

  String expects(int depth);

  String getValue();

  default void apply(ThresholdBuilder builder) {}
}
