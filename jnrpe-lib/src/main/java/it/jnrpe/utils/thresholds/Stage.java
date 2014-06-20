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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all the parsing stages. A parsing stage is composed of a name
 * and a set of named transitions. This way we can configure the parser as a
 * state machine.
 *
 * @author Massimiliano Ziccardi
 */
abstract class Stage {
    /**
     * The stage name.
     */
    private final String name;

    /**
     * The sets of stage transitions.
     */
    private Map<String, Stage> nextStagesMap = new HashMap<String, Stage>();

    /**
     * @param stageName
     *            The stage name
     */
    protected Stage(final String stageName) {
        name = stageName;
    }

    /**
     * Adds a possible transition to this stage.
     *
     * @param stage
     *            The transition
     */
    public void addTransition(final Stage stage) {
        nextStagesMap.put(stage.name, stage);
    }

    /**
     * Returns the name of this stage.
     *
     * @return The name of this stage
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a named transition of this stage.
     *
     * @param stageName
     *            The requested transition
     * @return The transition if found. Null otherwise.
     */
    public Stage getTransition(final String stageName) {
        return nextStagesMap.get(stageName);
    }

    /**
     * Returns the list of the possible transitions from this stage.
     *
     * @return the list of the possible transitions from this stage
     */
    public Set<String> getTransitionNames() {
        return nextStagesMap.keySet();
    }

    /**
     * A stage is a leaf if no transition are possible from here..
     *
     * @return <code>true</code> if no transitions are available from here.
     */
    public boolean isLeaf() {
        return this.nextStagesMap.isEmpty();
    }

    /**
     * Consumes a part of the threshold and configure the {@link RangeConfig}
     * object according to the swallowed part of the threshold.
     *
     * @param threshold
     *            The threshold to consume.
     * @param tc
     *            The threshold configuration object
     * @return The remaining unparsed part of the threshold
     * @throws RangeException
     *             -
     */
    public abstract String parse(final String threshold, RangeConfig tc) throws RangeException;

    /**
     * Tells if the current stage is able to parse the given threshold.
     * 
     * @param threshold
     *            The threshold to be parsed
     * @return The remaining tokens
     */
    public abstract boolean canParse(String threshold);

    /**
     * @return The list of tokens expected by this stage.
     */
    public abstract String expects();
}
