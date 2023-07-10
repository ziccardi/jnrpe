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

import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.engine.services.config.IConfigProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ConfigCommandRepository implements ICommandRepository {

  private final Map<String, ICommandDefinition> commandDefinitions = new HashMap<>();

  public ConfigCommandRepository() {
    IConfigProvider.getInstances().forEach(this::loadCommandsFromConfigProvider);
  }

  private void loadCommandsFromConfigProvider(IConfigProvider configProvider) {
    if (configProvider.getConfig().isPresent()) {
      var config = configProvider.getConfig().get();
      config
          .getCommands()
          .getDefinitions()
          .forEach(
              commandDefinition -> {
                this.commandDefinitions.put(commandDefinition.getName(), commandDefinition);
              });
    }
  }

  ConfigCommandRepository(Consumer<Map<String, ICommandDefinition>> commandDefinitionProvider) {
    commandDefinitionProvider.accept(this.commandDefinitions);
  }

  @Override
  public Collection<ICommandDefinition> getAllCommands() {
    return commandDefinitions.values();
  }

  @Override
  public Optional<ICommandDefinition> getCommand(String commandName) {
    return Optional.ofNullable(commandDefinitions.get(commandName));
  }
}
