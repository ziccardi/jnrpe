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

import java.util.Date;

/**
 * Event class to be used to send and receive JNRPE status events.
 * 
 * @author Massimiliano Ziccardi
 */
public class JNRPEStatusEvent implements IJNRPEEvent {
    
    /**
     * Enumeration of all the possible status.
     * @author Massimiliano Ziccardi
     *
     */
    public enum STATUS {
        /**
         * JNRPE server has started.
         */
        STARTED,
        /**
         * JNRPE Server has stopped.
         */
        STOPPED,
        /**
         * JNRPE Server start failed.
         */
        FAILED
    }
    
    /**
     * The status represented by this instance.
     */
    private final STATUS status;
    
    /**
     * The instant when the status has been reached.
     */
    private final Date instant = new Date();
    
    /**
     * The instance that sent the event.
     */
    private final Object source;
    
    /**
     * The event message.
     */
    private final String message;
    
    /**
     * Builds a JNRPE status event.
     * 
     * @param jnrpeStatus The reached status
     * @param eventSource The instance
     * @param eventMessage The message
     */
    public JNRPEStatusEvent(final STATUS jnrpeStatus, final Object eventSource, final String eventMessage) {
        this.status = jnrpeStatus;
        this.source = eventSource;
        this.message = eventMessage;
    }
    
    /**
     * @return the status represented by this object.
     */
    public final STATUS getStatus() {
        return status;
    }
    
    /**
     * @return the date when the status has been reached.
     */
    public final Date getInstant() {
        return (Date) instant.clone();
    }

    /**
     * @return the status name.
     */
    public final String getEventName() {
        return status.name();
    }

    /**
     * @return the event message.
     */
    public final String getMessage() {
        return message;
    }

    /**
     * @return the instance that generated the event.
     */
    public final Object getSource() {
        return source;
    }
}
