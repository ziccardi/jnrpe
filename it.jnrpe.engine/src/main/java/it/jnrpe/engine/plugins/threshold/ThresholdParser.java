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

import it.jnrpe.engine.plugins.threshold.statemachine.*;
import java.util.*;

/**
 * The {@link ThresholdParser} class is responsible for parsing a threshold string and constructing
 * a {@link IThreshold} object based on the parsed information.
 *
 * <p>If an exception is thrown during the parsing process, the exception's properties are set to
 * provide information about the error, such as the expected token, the index where the error
 * occurred, and the original threshold string. The exception is then thrown to the caller.
 *
 * <p>Threshold must follow the (see <a
 * href="https://nagios-plugins.org/doc/guidelines.html#THRESHOLDFORMAT">NAGIOS Threshold
 * syntax</a>)
 */
public class ThresholdParser {

  public static IThreshold parse(String thr) throws ThresholdParsingException {
    IParsingState currentState = StateMachineBuilder.build();

    Set<IParsingState> validationPath = new LinkedHashSet<>();

    int position = 1;
    try {
      for (char c : thr.toCharArray()) {
        currentState = currentState.parse(c);
        validationPath.add(currentState);
        position++;
      }

      if (!currentState.isValidEndState()) {
        throw new ThresholdEOSException();
      }

      var thresholdBuilder = new ThresholdBuilder();

      // the threshold is valid
      validationPath.forEach(state -> state.apply(thresholdBuilder));

      return thresholdBuilder.build();
    } catch (ThresholdParsingException ise) {
      ise.setExpectedToken(currentState.expects(1));
      ise.setIndex(position);
      ise.setThresholdString(thr);
      throw ise;
    }
  }
}
