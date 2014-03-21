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

import java.util.Set;

/**
 * This is an utility class than can be used to send simply events to all the
 * registered listeners.
 *
 * @author Massimiliano Ziccardi
 */
public final class EventsUtil {

    /**
     * Private default constructor.
     */
    private EventsUtil() {

    }

    /**
     * This method sends log events to the registered listeners. It is an.
     * utility method that relieve the programmer from the need to create all.
     * the EventParam object just to send log events. Supported event type are.
     * TRACE, DEBUG, INFO, WARNING, ERROR, FATAL.
     *
     * @param listenersList
     *            The list of all the listeners that will receive the event
     * @param sender
     *            The sender of the event (usually <code>this</code>)
     * @param evt
     *            The event type
     * @param message
     *            The log message
     */
    public static void sendEvent(final Set<IJNRPEEventListener> listenersList,
            final Object sender, final LogEvent evt, final String message) {
        if (listenersList == null || listenersList.isEmpty()) {
            return;
        }

        sendEvent(listenersList, sender, evt, message, null);
    }

    /**
     * This method sends log events to the registered listeners. It is an.
     * utility method that relieve the programmer from the need to create all.
     * the EventParam object just to send log events. Supported event type are.
     * TRACE, DEBUG, INFO, WARNING, ERROR, FATAL.
     *
     * @param listenerList
     *            The list of all the listeners that will receive the event
     * @param sender
     *            The sender of the event (usually <code>this</code>)
     * @param evt
     *            The event type
     * @param message
     *            The log message
     * @param exception
     *            The exception to be, eventually, logged (can be null).
     */
    public static void sendEvent(final Set<IJNRPEEventListener> listenerList,
            final Object sender, final LogEvent evt, final String message,
            final Throwable exception) {
        if (listenerList == null || listenerList.isEmpty()) {
            return;
        }

        if (sender == null || evt == null || message == null) {
            throw new NullPointerException(
                    "The sender, evt and message parameter can't be null");
        }

        if (exception != null) {
            sendEvent(listenerList, sender, evt.name(), new EventMessageParam(
                    message), new EventExceptionParam(exception));
        } else {
            sendEvent(listenerList, sender, evt.name(), new EventMessageParam(
                    message));
        }
    }

    /**
     * This method is used to send custom events to the registered listeners.
     * The event type can be a freely chosen string. A custom listener should be
     * instructed to handle such event with its parameters.
     *
     * @param listenerList
     *            The list of all the listeners that will receive the event
     * @param sender
     *            The sender of the event (usually <code>this</code>)
     * @param customEvtType
     *            The custom event type
     * @param paramsList
     *            The event parameters
     */
    public static void sendEvent(final Set<IJNRPEEventListener> listenerList,
            final Object sender, final String customEvtType,
            final EventParam... paramsList) {
        if (sender == null || customEvtType == null) {
            throw new NullPointerException(
                    "The sender and event type parameter can't be null");
        }

        if (listenerList == null || listenerList.isEmpty()) {
            return;
        }

        SimpleEvent se = new SimpleEvent(customEvtType, paramsList);

        for (IJNRPEEventListener listener : listenerList) {
            listener.receive(sender, se);
        }
    }
}
