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
import it.jnrpe.engine.services.plugins.CommandLine.Command;
import it.jnrpe.engine.services.plugins.CommandLine.Option;
import it.jnrpe.engine.services.plugins.IPlugin;

@Command(
    name = "CHECK_DISK",
    description =
        "This plugin checks the amount of used disk space on a mounted file system and generates an alert if free space is less than one of the threshold values",
    footer = "Copyright(c) 2020 Massimiliano Ziccardi")
public class CheckDiskPlugin implements IPlugin {
  @Option(
      names = {"-w", "--warning"},
      paramLabel = "INTEGER[%]",
      description =
          "Exit with WARNING status if less than INTEGER units of disk are free or less than PERCENT of disk space is free")
  String warning;

  @Option(
      names = {"-c", "--critical"},
      paramLabel = "INTEGER[%]",
      description =
          "Exit with CRITICAL status if less than INTEGER units of disk are free or less than PERCENT of disk space is free")
  String critical;

  @Override
  public String getName() {
    return "CHECK_DISK";
  }

  @Override
  public ExecutionResult execute() {
    // TODO: implement
    return new ExecutionResult("CHECK_DISK Invoiked", Status.OK);
  }
}
