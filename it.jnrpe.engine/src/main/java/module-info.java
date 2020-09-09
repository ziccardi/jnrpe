import it.jnrpe.engine.commands.embedded.EmbeddedCommandsRepository;
import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.engine.services.config.IConfigProvider;

module it.jnrpe.engine {
  exports it.jnrpe.engine.services.commands;
  exports it.jnrpe.engine.services.config;
  exports it.jnrpe.engine.services.events;
  exports it.jnrpe.engine.services.network;
  exports it.jnrpe.engine.services.plugins;

  uses IConfigProvider;
  uses ICommandRepository;

  provides ICommandRepository with
      EmbeddedCommandsRepository;
}
