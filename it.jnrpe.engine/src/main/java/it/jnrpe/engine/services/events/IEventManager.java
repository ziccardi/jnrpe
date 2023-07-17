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
package it.jnrpe.engine.services.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * The interface for an event manager.
 *
 * <p>This interface provides the methods every event manager provider has to implement.
 */
public interface IEventManager {
  /**
   * Gets a collection of all event managers.
   *
   * @return A collection of all event managers.
   */
  static Collection<IEventManager> getProviders() {
    ServiceLoader<IEventManager> services = ServiceLoader.load(IEventManager.class);
    return services.stream()
        .map(ServiceLoader.Provider::get)
        // TODO: This is to make the tests run. Tests should be refactored to not require a mutable
        // list
        .collect(Collectors.toCollection(ArrayList<IEventManager>::new));
  }

  /**
   * Dispatches an event.
   *
   * @param type The type of event.
   * @param message The message of the event.
   */
  void onEvent(IEventType type, String message);

  /**
   * Dispatches an event with an exception.
   *
   * @param type The type of event.
   * @param message The message of the event.
   * @param exc The exception associated with the event.
   */
  void onEvent(IEventType type, String message, Throwable exc);
}
