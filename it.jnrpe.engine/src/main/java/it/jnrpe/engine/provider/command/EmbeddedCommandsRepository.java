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

import it.jnrpe.engine.services.commands.ICommandDefinition;
import it.jnrpe.engine.services.commands.ICommandRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EmbeddedCommandsRepository implements ICommandRepository {

  private final Map<String, ICommandDefinition> commands = new HashMap<>();

  public EmbeddedCommandsRepository() {
    commands.put(NRPECheckCommand.NAME, new NRPECheckCommand());
  }

  @Override
  public Optional<ICommandDefinition> getCommand(String commandName) {
    return Optional.ofNullable(commands.get(commandName));
  }

  @Override
  public Collection<ICommandDefinition> getAllCommands() {
    return commands.values();
  }
}
