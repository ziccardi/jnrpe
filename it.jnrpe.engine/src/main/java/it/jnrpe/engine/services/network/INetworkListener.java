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
package it.jnrpe.engine.services.network;

import it.jnrpe.engine.services.config.IJNRPEConfig;

/**
 * The interface every netwr listener must implement.
 *
 * <p>This interface provides methods for getting the name of the listener, binding to a port,
 * shutting down the listener, and checking if the listener supports a binding.
 */
public interface INetworkListener {

  /**
   * Gets the name of the listener.
   *
   * @return The name of the listener.
   */
  String getName();

  /**
   * Binds the listener to a port.
   *
   * @param binding The binding to bind to.
   */
  void bind(IJNRPEConfig.Binding binding);

  /** Shuts down the listener. */
  void shutdown();

  /**
   * Checks if the listener supports a binding.
   *
   * @param binding The binding to check.
   * @return True if the listener supports the binding, false otherwise.
   */
  boolean supportBinding(IJNRPEConfig.Binding binding);
}
