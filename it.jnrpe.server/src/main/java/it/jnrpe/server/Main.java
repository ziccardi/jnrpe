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
package it.jnrpe.server;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.events.LogEvent;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.CommandLine.Command;
import it.jnrpe.server.commands.StartCommand;

@Command(
    name = "jnrpe",
    subcommands = {StartCommand.class})
public class Main {
  @CommandLine.Option(
      names = {"-c", "--conf"},
      defaultValue = "/etc/jnrpe/jnrpe.yml",
      paramLabel = "PATH",
      required = true,
      description = "Path to the JNRPE server config file (defaults to '/etc/jnrpe/jnrpe.yml')")
  private String confFile;

  public String getConfigFilePath() {
    return this.confFile;
  }

  public static void main(String[] args) {
    CommandLine cli = new CommandLine(new Main());

    try {
      cli.execute(args);
    } catch (Exception e) {
      // TODO replace with a 'fatal'
      EventManager.emit(LogEvent.FATAL, "Unable to start JNRPE", e);
      cli.usage(System.out);
    }
  }
}
