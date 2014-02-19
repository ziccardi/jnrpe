/*
 * Copyright (c) 2012 Massimiliano Ziccardi
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
 */
package it.jnrpe;

import java.util.HashMap;
import java.util.Map;

import it.jnrpe.events.IJNRPEEvent;

/**
 * A basic, very simple, general purpose event.
 *
 * @author Massimiliano Ziccardi
 */
class SimpleEvent implements IJNRPEEvent {
    /**
     * The name of the event.
     */
    private String eventName = null;

    /**
     * A map containing all the event parameters. The key of the map is the
     * parameter name.
     */
    private Map<String, Object> eventParametersMap =
            new HashMap<String, Object>();

    /**
     * Initializes the event with the given event name and the given event
     * parameters.
     *
     * @param sEventName
     *            The event name
     * @param paramsAry
     *            The event parameters
     */
    public SimpleEvent(final String sEventName, final Object[] paramsAry) {
        this.eventName = sEventName;
        for (int i = 0; paramsAry != null && i < paramsAry.length; i += 2) {
            eventParametersMap.put((String) paramsAry[i], paramsAry[i + 1]);
        }
    }

    /**
     * Returns the event name.
     *
     * @return The event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Returns the event parameters.
     *
     * @return The event parameters
     */
    public Map<String, Object> getEventParams() {
        return eventParametersMap;
    }
}
