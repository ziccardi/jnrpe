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

/**
 * The event bus that will dispatch all the events regarding JNRPE.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public interface IJNRPEEventBus {

    /**
     * Registers an event listener.
     * 
     * The method that will receive the event must be marked with the {@link com.google.common.eventbus.Subscribe} annotation.
     * @param object the event listener.
     */
    void register(final Object object);
    
    /**
     * This method must be called to post a new event.
     * 
     * @param event The vent object
     */
    void post(final Object event);
}