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

import it.jnrpe.plugins.IPluginInterface;

import com.google.common.eventbus.EventBus;

/**
 * The JNRPE event bus implementation.
 * 
 * @author Massimiliano Ziccardi
 *
 * @version $Revision: 1.0 $
 */
public class JNRPEEventBus extends EventBus implements IJNRPEEventBus {
    
    /**
     * Method register.
     * @param object Object
     * @see it.jnrpe.IJNRPEEventBus#register(Object)
     */
    @Override
    /**
     * Register an object as listener.
     * An exception is thrown if the listener is a plugin, since plugins can't be registered as listeners (otherwise they would never
     * get garbage collected).
     *  
     */
    public final void register(final Object object) {
        if (object instanceof IPluginInterface) {
            throw new IllegalArgumentException("Plugins can't be registered as listeners");
        }
        super.register(object);
    }
    
    /**
     * This method must be called to post a new event.
     * 
     * @param event The vent object
     * @see it.jnrpe.IJNRPEEventBus#post(Object)
     */
    public final void post(final Object event) {
        super.post(event);
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "JNRPEEventBus []";
    }
}
