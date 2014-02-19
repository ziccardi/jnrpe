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
package it.jnrpe.osgi;

import it.jnrpe.events.IJNRPEEvent;
import it.jnrpe.events.IJNRPEEventListener;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JNRPE Server uses this event listener to populate the log files.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class EventLoggerListener implements IJNRPEEventListener {
    /**
     * All the loggers.
     */
    private final Map<String, Logger> loggersMap = new HashMap<String, Logger>();

    /**
     * This method receives the log event and logs it.
     *
     * @param sender
     *            The source of the event
     * @param event
     *            The event
     */
    public final void receive(final Object sender, final IJNRPEEvent event) {
        String sClassName = sender.getClass().getName();

        Logger logger = loggersMap.get(sClassName);

        if (logger == null) {
            logger = LoggerFactory.getLogger(sender.getClass());
            loggersMap.put(sClassName, logger);
        }

        Throwable error = (Throwable) event.getEventParams().get("EXCEPTION");

        //System.out.println((String) event.getEventParams().get("MESSAGE"));
        if (error != null) {
            error.printStackTrace();
        }

        String sEventName = event.getEventName();

        if (sEventName.equals("TRACE")) {
            if (error != null) {
                logger.trace((String) event.getEventParams().get("MESSAGE"),
                        error);
            } else {
                logger.trace((String) event.getEventParams().get("MESSAGE"));
            }
            return;
        }

        if (sEventName.equals("DEBUG")) {
            if (error != null) {
                logger.debug((String) event.getEventParams().get("MESSAGE"),
                        error);
            } else {
                logger.debug((String) event.getEventParams().get("MESSAGE"));
            }
            return;
        }

        if (sEventName.equals("INFO")) {
            if (error != null) {
                logger.info((String) event.getEventParams().get("MESSAGE"),
                        error);
            } else {
                logger.info((String) event.getEventParams().get("MESSAGE"));
            }
            return;
        }

        if (sEventName.equals("WARNING")) {
            if (error != null) {
                logger.warn((String) event.getEventParams().get("MESSAGE"),
                        error);
            } else {
                logger.warn((String) event.getEventParams().get("MESSAGE"));
            }
            return;
        }

        if (sEventName.equals("ERROR")) {
            if (error != null) {
                logger.error((String) event.getEventParams().get("MESSAGE"),
                        error);
            } else {
                logger.error((String) event.getEventParams().get("MESSAGE"));
            }
            return;
        }

        if (sEventName.equals("FATAL")) {
            if (error != null) {
                logger.error((String) event.getEventParams().get("MESSAGE"),
                        (Throwable) (event.getEventParams().get("EXCEPTION")));
            } else {
                logger.error((String) event.getEventParams().get("MESSAGE"));
            }
            return;
        }
    }

}
