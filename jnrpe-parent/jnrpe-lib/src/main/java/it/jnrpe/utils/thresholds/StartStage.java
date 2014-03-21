/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
package it.jnrpe.utils.thresholds;

/**
 * The root of the parsing state machine.
 *
 * @author Massimiliano Ziccardi
 */
class StartStage extends Stage {

    /**
     *
     */
    protected StartStage() {
        super("root");
    }

    @Override
    public boolean canParse(final String threshold) {
        return false;
    }

    @Override
    public String parse(final String threshold, final RangeConfig tc)
            throws RangeException {

        if (threshold == null) {
            throw new RangeException("Range can't be null");
        }

        Stage currentStage = this;

        String parsedThreshold = threshold;

        boolean stageParsed;

        while (parsedThreshold.length() != 0) {
            stageParsed = false;
            for (String transitionName : currentStage.getTransitionNames()) {
                Stage transition = currentStage.getTransition(transitionName);
                if (transition.canParse(parsedThreshold)) {
                    parsedThreshold = transition.parse(parsedThreshold, tc);
                    currentStage = transition;
                    stageParsed = true;
                    break;
                }
            }

            if (!stageParsed) {
                throw new InvalidRangeSyntaxException(currentStage,
                        parsedThreshold, threshold);
            }
        }

        if (!currentStage.isLeaf()) {
            throw new PrematureEndOfRangeException(currentStage, threshold);
        }

        return parsedThreshold;
    }

    @Override
    public String expects() {
        return null;
    }
}
