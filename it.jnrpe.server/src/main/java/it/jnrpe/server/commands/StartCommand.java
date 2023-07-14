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
package it.jnrpe.server.commands;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.Binding;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IJNRPEConfig;
import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.server.ConfigSource;
import it.jnrpe.server.Main;
import java.io.File;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "start", description = "Starts the JNRPE server")
public class StartCommand implements Callable<Void> {

  @CommandLine.ParentCommand private Main jnrpe;

  private void bind(Binding binding) {
    ServiceLoader.load(INetworkListener.class).stream()
        .map(ServiceLoader.Provider::get)
        .filter(l -> l.supportBinding(binding))
        .findFirst()
        .ifPresentOrElse(
            netListener -> {
              if (netListener.supportBinding(binding)) {
                EventManager.info(
                    "BindingConfigProxy on port %d using network provider named '%s'",
                    binding.port(), netListener.getName());
                netListener.bind(binding);
              }
            },
            () -> {
              EventManager.fatal("No network services has been found");
              System.exit(-1);
            });
  }

  @Override
  public Void call() {
    // Parsing the configuration
    File confFile = new File(jnrpe.getConfigFilePath());
    if (!confFile.canRead()) {
      EventManager.fatal("Unable to read the configuration file at %s", confFile.getAbsolutePath());
      return null;
    }

    ConfigSource.setConfigFile(confFile);

    ServiceLoader<IConfigProvider> configProviderServiceLoader =
        ServiceLoader.load(IConfigProvider.class);

    Optional<IJNRPEConfig> config = Optional.empty();

    for (IConfigProvider configProvider : configProviderServiceLoader) {
      Optional<IJNRPEConfig> conf = configProvider.getConfig();
      if (conf.isPresent()) {
        config = conf;
        break;
      }
    }

    config.ifPresentOrElse(
        cfg -> cfg.getServer().bindings().forEach(this::bind),
        () ->
            EventManager.fatal(
                "No config provider has been able to parse the provided config file (%s)",
                confFile.getAbsolutePath()));
    return null;
  }
}
