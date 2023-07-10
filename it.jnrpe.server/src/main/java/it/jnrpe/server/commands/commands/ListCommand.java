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
package it.jnrpe.server.commands.commands;

import it.jnrpe.engine.commands.CommandRepository;
import it.jnrpe.engine.services.plugins.CommandLine;

@CommandLine.Command(name = "list", description = "List all configured commands")
public class ListCommand implements Runnable {
  @Override
  public void run() {
    System.out.println("List of available commands:");
    CommandRepository.getInstance()
        .getAllCommands()
        .forEach(command -> System.out.println("  * " + command.getName()));
  }
}
