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

/**
 * The interface for an event type.
 *
 * <p>This interface provides methods for getting the ordinal and type of an event type.
 */
public interface IEventType {
  /**
   * Gets the ordinal of the event type.
   *
   * @return The ordinal of the event type.
   */
  int ordinal();

  /**
   * Gets the type of the event type.
   *
   * @return The type of the event type.
   */
  String type();
}
