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
package it.jnrpe.engine.services.commands;

import it.jnrpe.engine.services.network.Status;

public class ExecutionResult {
  private final String message;
  private final Status status;

  public static final ExecutionResult GENERAL_CHECK_ERROR =
      new ExecutionResult("Internal check error", Status.UNKNOWN);

  public ExecutionResult(final String message, final Status status) {
    this.message = message;
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public static ExecutionResult errorExecutingCommand(final String commandName) {
    return new ExecutionResult(
        String.format("[%s - UNKNOWN] - Error executing command", commandName), Status.UNKNOWN);
  }
}
