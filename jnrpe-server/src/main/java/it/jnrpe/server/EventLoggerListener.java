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
package it.jnrpe.server;

import it.jnrpe.events.LogEvent;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

/**
 * The JNRPE Server uses this event listener to populate the log files.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
public class EventLoggerListener {
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
    @Subscribe
    public final void receive(final LogEvent logEvent) {
        final String sClassName = logEvent.getSource().getClass().getName();

        Logger logger = loggersMap.get(sClassName);

        if (logger == null) {
            logger = LoggerFactory.getLogger(logEvent.getSource().getClass());
            loggersMap.put(sClassName, logger);
        }

        final Throwable error = logEvent.getCause();

        switch (logEvent.getLogType()) {
        case TRACE:
            logger.trace(logEvent.getMessage(), error);
            break;
        case DEBUG:
            logger.debug(logEvent.getMessage(), error);
            break;
        case INFO:
            logger.info(logEvent.getMessage(), error);
            break;
        case WARNING:
            logger.warn(logEvent.getMessage(), error);
            break;
        case ERROR:
            logger.error(logEvent.getMessage(), error);
            break;
        case FATAL:
            logger.error(logEvent.getMessage(), error);
            break;
        }
    }

}
