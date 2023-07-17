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
package it.jnrpe.engine.services.plugins;

import it.jnrpe.engine.services.commands.ExecutionResult;

/**
 * The interface for a plugin.
 *
 * <p>This interface provides methods for getting the name of the plugin and executing the plugin.
 */
public interface IPlugin {

  /**
   * Gets the name of the plugin.
   *
   * @return The name of the plugin.
   */
  String getName();

  /**
   * Executes the plugin.
   *
   * @return The result of executing the plugin.
   */
  ExecutionResult execute();
}
