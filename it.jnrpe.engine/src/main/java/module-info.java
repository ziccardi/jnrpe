import it.jnrpe.engine.provider.command.ConfigCommandRepository;
import it.jnrpe.engine.provider.command.EmbeddedCommandsRepository;
import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.engine.services.plugins.IPluginRepository;

module it.jnrpe.engine {
  exports it.jnrpe.engine.services.commands;
  exports it.jnrpe.engine.services.config;
  exports it.jnrpe.engine.services.events;
  exports it.jnrpe.engine.services.network;
  exports it.jnrpe.engine.services.plugins;
  exports it.jnrpe.engine.events;
  exports it.jnrpe.engine.plugins;

  uses IConfigProvider;
  uses ICommandRepository;
  uses IPluginRepository;
  uses IEventManager;

  provides ICommandRepository with
      EmbeddedCommandsRepository,
      ConfigCommandRepository;
}
