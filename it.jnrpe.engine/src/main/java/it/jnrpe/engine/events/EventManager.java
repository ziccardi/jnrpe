/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.engine.events;

import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.engine.services.events.IEventType;
import java.util.Collection;

public class EventManager {
  private static Collection<IEventManager> eventManagers = IEventManager.getInstances();

  public static void emit(IEventType type, String message) {
    eventManagers.forEach(eventManager -> eventManager.onEvent(type, message));
  }

  public static void emit(IEventType type, String message, Throwable exc) {
    eventManagers.forEach(eventManager -> eventManager.onEvent(type, message, exc));
  }
}
