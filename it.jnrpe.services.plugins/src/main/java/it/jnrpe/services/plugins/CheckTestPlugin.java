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
package it.jnrpe.services.plugins;

import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.IPlugin;

@CommandLine.Command(name = "CHECK_TEST")
public class CheckTestPlugin implements IPlugin {
  private static final String NAME = "CHECK_TEST";

  @CommandLine.Option(names = {"-m", "--message"}, required = true)
  private String message;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public ExecutionResult execute() {
    return new ExecutionResult(this.message, Status.OK);
  }
}
