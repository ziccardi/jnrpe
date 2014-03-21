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
package it.jnrpe.events;

/**
 * This class represent an event parameter. An event parameter can be of any
 * type, but must have a unique name.
 *
 * @author Massimiliano Ziccardi
 */
public class EventParam {
    /**
     * The parameters name.
     */
    private final String paramName;
    /**
     * The parameter value.
     */
    private final Object paramValue;

    /**
     * Builds and initializes an event parameter.
     *
     * @param name
     *            The parameter name
     * @param value
     *            The parameter value
     */
    public EventParam(final String name, final Object value) {
        paramName = name;
        paramValue = value;
    }

    /**
     * Returns the parameter name.
     *
     * @return The parameter name
     */
    public final String getName() {
        return paramName;
    }

    /**
     * Returns the parameter value.
     *
     * @return The parameter value
     */
    public final Object getValue() {
        return paramValue;
    }
}
