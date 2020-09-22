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
import java.math.BigDecimal;
import java.util.*;

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

  private static void test(String thr, BigDecimal value, boolean expected) throws Exception {
    IThreshold threshold = parse(thr);
    if (threshold.fallsInside(value) == expected) {
      System.out.println(thr + " PASSED");
    } else {
      System.out.println(thr + " FAILED");
    }
  }

  public static void main(String[] args) throws Exception {
    try {
      test("-10.5:20.9", new BigDecimal(-20), false);
      test("-10.5:20.9", new BigDecimal(-1), true);
      test("-10.5:20.9", new BigDecimal(0), true);
      test("-10.5:20.9", new BigDecimal(5), true);
      test("-10.5:20.9", new BigDecimal(10), true);
      test("-10.5:20.9", new BigDecimal("20.9"), true);
      test("-10.5:20.9", new BigDecimal("20.91"), false);
    } catch (ThresholdParsingException tse) {
      System.err.println(tse.getThresholdString());
      for (int i = 1; i < tse.getIndex(); i++) {
        System.err.print(" ");
      }
      System.err.println("^");
      System.err.printf("Unexpected token. Expecting one of '%s'\n", tse.getExpectedToken());
      tse.printStackTrace();
    }
  }
}
