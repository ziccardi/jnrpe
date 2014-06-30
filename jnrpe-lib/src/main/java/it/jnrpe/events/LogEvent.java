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
 * LogEvent object. Used to send log mesages.
 * 
 * @author Massimiliano Ziccardi
 *
 */
public final class LogEvent implements IJNRPEEvent {
   
    /**
     * All the accepted Log Event types.
     *
     * @author Massimiliano Ziccardi
     */
    public enum LogEventType {
        /**
         * Trace Log Event.
         */
        TRACE,
        /**
         * Debug Log Event.
         */
        DEBUG,
        /**
         * Info Log Event.
         */
        INFO,
        /**
         * Warning Log Event.
         */
        WARNING,
        /**
         * Error Log Event.
         */
        ERROR,
        /**
         * Fatal Log Event.
         */
        FATAL;
    };

    /**
     * The log severity.
     */
    private final LogEventType logType;
    
    /**
     * The log message.
     */
    private final String message;
    
    /**
     * The error cause (if any).
     */
    private final Throwable error;
    
    /**
     * The instance that generated the log.
     */
    private final Object source;
    
    /**
     * Generates a new LogEvent object.
     * 
     * @param evtSource the originator of the event
     * @param type the event type
     * @param logMessage the event message
     * @param cause the cause (if any)
     */
    public LogEvent(final Object evtSource, final LogEventType type, final String logMessage, final Throwable cause) {
        this.logType = type;
        this.message = logMessage;
        this.error = cause;
        this.source = evtSource;
    }

    /**
     * Generates a new LogEvent object.
     * 
     * @param evtSource the originator of the event
     * @param type the event type
     * @param logMessage the event message
     */
    public LogEvent(final Object evtSource, final LogEventType type, final String logMessage) {
        this.logType = type;
        this.message = logMessage;
        this.error = null;
        this.source = evtSource;
    }
    
    /**
     * @return the event type
     */
    public LogEventType getLogType() {
        return logType;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the cause
     */
    public Throwable getCause() {
        return error;
    }
    
    /**
     * @return the event originator
     */
    public Object getSource() {
        return source;
    }

    /**
     * @return the event name
     */
    public String getEventName() {
        return getLogType().name();
    }
    
}

