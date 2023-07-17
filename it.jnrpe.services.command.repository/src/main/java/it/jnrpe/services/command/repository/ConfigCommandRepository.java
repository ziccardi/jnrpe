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
package it.jnrpe.services.command.repository;

import it.jnrpe.engine.services.commands.ICommandFactory;
import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.engine.services.config.CommandInitializer;
import it.jnrpe.engine.services.config.IConfigProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The class for a command repository that loads commands from the configuration.
 *
 * <p>This class implements the {@link ICommandRepository} interface and loads commands from the
 * configuration.
 */
public class ConfigCommandRepository implements ICommandRepository {

  private final Map<String, ICommandFactory> commandDefinitions = new HashMap<>();

  /**
   * Creates a new command repository.
   *
   * <p>Loads commands from all configured command providers.
   */
  public ConfigCommandRepository() {
    IConfigProvider.getProviders().forEach(this::loadCommandsFromConfigProvider);
  }

  private void loadCommandsFromConfigProvider(IConfigProvider configProvider) {
    if (configProvider.getConfig().isPresent()) {
      var config = configProvider.getConfig().get();
      config
          .getCommands()
          .forEach(
              commandConfig -> {
                this.commandDefinitions.put(
                    commandConfig.name(), new CommandInitializer(commandConfig));
              });
    }
  }

  /**
   * Creates a new command repository with a custom command definition provider.
   *
   * @param commandDefinitionProvider The command definition provider.
   */
  ConfigCommandRepository(Consumer<Map<String, ICommandFactory>> commandDefinitionProvider) {
    commandDefinitionProvider.accept(this.commandDefinitions);
  }

  @Override
  public Collection<ICommandFactory> getAllCommands() {
    return commandDefinitions.values();
  }

  @Override
  public Optional<ICommandFactory> getCommand(String commandName) {
    return Optional.ofNullable(commandDefinitions.get(commandName));
  }
}
