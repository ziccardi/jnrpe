/*
 * Copyright (c) 2012 Massimiliano Ziccardi Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package it.jnrpe.events;

/**
 * This class represent the interface an object must implement to be able to
 * receive events from JNRPE.
 *
 * @author Massimiliano Ziccardi
 */
public interface IJNRPEEventListener {
    /**
     * This method receives the event and reacts.
     *
     * @param sender
     *            The source of the event
     * @param event
     *            The event
     */
    void receive(Object sender, IJNRPEEvent event);
}
