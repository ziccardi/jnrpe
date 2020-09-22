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

public class StateMachineBuilder {
  public static IParsingState build() {
    IParsingState negateState = new NegateState(); // @
    IParsingState negativeInfinityState = new NegativeInfinity(); // ~
    IParsingState minusSignMinEdgeState = new MinusState(); // /
    NumberState minEdgeState = new NumberState(); // [0-9]

    // Colon: we can have a colon in 2 positions:
    // * as first character or after ~ or @ (es: :10, @:10, ~:10) : in this case it is not a valid
    // final state
    // * after the min edge limit (ie: 10:) : in this case it is a valid final state
    IParsingState colonBeforeMinEdgeState = new ColonState(false, minEdgeState);
    IParsingState colonAfterminEdgeState = new ColonState(true, minEdgeState);

    // After the colon
    IParsingState minusSignMaxEdgeState = new MinusState(); // -
    IParsingState maxEdgeState = new NumberState(); // [0-9]

    // State machine

    negateState
        .addTransition(colonBeforeMinEdgeState)
        .addTransition(minusSignMinEdgeState)
        .addTransition(minEdgeState)
        .addTransition(negativeInfinityState);

    negativeInfinityState.addTransition(colonBeforeMinEdgeState);

    colonBeforeMinEdgeState.addTransition(maxEdgeState).addTransition(minusSignMaxEdgeState);

    colonAfterminEdgeState.addTransition(maxEdgeState).addTransition(minusSignMaxEdgeState);

    minEdgeState.addTransition(colonAfterminEdgeState);

    minusSignMinEdgeState.addTransition(minEdgeState);

    minusSignMaxEdgeState.addTransition(maxEdgeState);

    // A threshold can start with either a ':', a '@', a '~', a '-' or a number of digits
    return new StartState()
        .addTransition(colonBeforeMinEdgeState)
        .addTransition(negateState)
        .addTransition(minusSignMinEdgeState)
        .addTransition(minEdgeState)
        .addTransition(negativeInfinityState);
  }
}
