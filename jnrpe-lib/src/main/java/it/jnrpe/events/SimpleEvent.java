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

import java.util.HashMap;
import java.util.Map;

/**
 * A basic, very simple, general purpose event.
 *
 * @author Massimiliano Ziccardi
 */
class SimpleEvent implements IJNRPEEvent {
    /**
     * The log event associated with this object.
     */
    private LogEvent logEventType = null;

    /**
     * The parameters of the vent.
     */
    private Map<String, Object> parametersMap = new HashMap<String, Object>();

    /**
     * The custom event type (if it is not a log event).
     */
    private String customEventType = null;

    /**
     * Builds the {@link SimpleEvent} object with a LogEvent.
     *
     * @param evt
     *            The Log Event
     * @param paramsAry
     *            The Log Event parameters
     */
    public SimpleEvent(final LogEvent evt, final Object[] paramsAry) {
        if (evt == null) {
            throw new NullPointerException("Event type can't be null");
        }
        logEventType = evt;
        for (int i = 0; paramsAry != null && i < paramsAry.length; i += 2) {
            parametersMap.put((String) paramsAry[i], paramsAry[i + 1]);
        }
    }

    /**
     * Builds the {@link SimpleEvent} object with a custom event type.
     *
     * @param custEvtType
     *            The Custom Event Type
     * @param paramsAry
     *            The Event parameters
     */
    public SimpleEvent(final String custEvtType, final Object[] paramsAry) {
        if (custEvtType == null) {
            throw new NullPointerException("Event type can't be null");
        }

        customEventType = custEvtType;
        for (int i = 0; paramsAry != null && i < paramsAry.length; i += 2) {
            EventParam param = (EventParam) paramsAry[i];
            parametersMap.put(param.getName(), param.getValue());
        }
    }

    /**
     * Returns the event name.
     *
     * @return The event name
     */
    public String getEventName() {
        if (customEventType != null) {
            return customEventType;
        }
        return logEventType.name();
    }

    /**
     * Returns the LogEvent type (<code>null</code> if it is not a log event).
     *
     * @return The LogEvent type (<code>null</code> if it is not a log event).
     */
    LogEvent getLogEvent() {
        return logEventType;
    }

    /**
     * Returns the event parameters.
     *
     * @return The event parameters
     */
    public Map<String, Object> getEventParams() {
        return parametersMap;
    }

}
