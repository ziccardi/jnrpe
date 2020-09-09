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
package it.jnrpe.engine.provider.command;

import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandInstance;
import it.jnrpe.engine.services.network.Status;

public class NRPECheckCommand implements ICommandDefinition {
  public static String NAME = "_NRPE_CHECK";

  private static class NRPECheckCommandInstance implements ICommandInstance {
    @Override
    public ExecutionResult execute() {
      return new ExecutionResult("JNRPE v3.0.0", Status.OK);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public ICommandInstance instantiate(String... params) {
    return new NRPECheckCommandInstance();
  }
}
