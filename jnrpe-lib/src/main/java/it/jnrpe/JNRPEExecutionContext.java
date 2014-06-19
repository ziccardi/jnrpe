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
package it.jnrpe;

import it.jnrpe.events.IJNRPEEventListener;

import java.nio.charset.Charset;
import java.util.Collection;

/**
 * A context to be passed to all the object managing the communication.
 * 
 * @author Massimiliano Ziccardi
 */
public class JNRPEExecutionContext {

    /**
     * The list of listeners.
     */
    private final Collection<IJNRPEEventListener> eventListenersList;

    /**
     * Configured charset.
     */
    private final Charset charset;

    /**
     * Builds and initializes the context.
     * 
     * @param eventListeners the list of listeners
     * @param currentCharset the configured charset
     */
    JNRPEExecutionContext(final Collection<IJNRPEEventListener> eventListeners, final Charset currentCharset) {
        this.eventListenersList = eventListeners;
        this.charset = currentCharset;
    }

    /**
     * Returns all the listeners.
     * @return the listeners
     */
    public final Collection<IJNRPEEventListener> getListeners() {
        return eventListenersList;
    }

    /**
     * Returns the charset.
     * @return the configured charset
     */
    public final Charset getCharset() {
        return charset;
    }
}
