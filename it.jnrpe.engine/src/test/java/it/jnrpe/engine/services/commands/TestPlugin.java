/*******************************************************************************
 * Copyright (C) 2023, Massimiliano Ziccardi
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

import static it.jnrpe.engine.services.plugins.CommandLine.*;

import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.IPlugin;

@Command(name = "TEST_PLUGIN")
public class TestPlugin implements IPlugin {

  @Option(
      names = {"-a", "--option1"},
      required = true)
  public String option1;

  @Option(names = {"-b", "--option2"})
  public String option2;

  @Option(names = {"-c", "--option3"})
  public String option3;

  @Option(names = {"-d", "--option4"})
  public String option4;

  @Override
  public String getName() {
    return "TEST_PLUGIN";
  }

  @Override
  public ExecutionResult execute() {
    return new ExecutionResult("Executed", Status.OK);
  }
}
