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
package it.jnrpe.server.commands;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.plugins.CommandLine;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "init", description = "Creates a basic JNRPE config file")
public class InitCommand implements Callable<Void> {

  @CommandLine.Option(
      names = {"--format"},
      defaultValue = "yaml",
      paramLabel = "format",
      required = true,
      description = "The format to be used to generate the JNRPE config")
  String format;

  @Override
  public Void call() {
    IConfigProvider.getProviders().stream()
        .filter(p -> p.getProviderName().equalsIgnoreCase(this.format))
        .findFirst()
        .ifPresentOrElse(
            p -> {
              System.out.println(p.generateSampleConfig());
            },
            () -> {
              String availableFormats =
                  IConfigProvider.getProviders().stream()
                      .map(IConfigProvider::getProviderName)
                      .collect(Collectors.joining(","));

              EventManager.fatal(
                  "No provider is able to generate a configuration in the '%s' format. Available formats: [%s]",
                  this.format, availableFormats);
            });
    return null;
  }
}
