import it.jnrpe.engine.provider.auth.SimpleAuthProvider;
import it.jnrpe.engine.provider.command.EmbeddedCommandsRepository;
import it.jnrpe.engine.services.auth.IAuthService;
import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.engine.services.plugins.IPluginRepository;

module it.jnrpe.engine {
  requires org.apache.commons.text;

  exports it.jnrpe.engine.services.commands;
  exports it.jnrpe.engine.services.config;
  exports it.jnrpe.engine.services.events;
  exports it.jnrpe.engine.services.network;
  exports it.jnrpe.engine.services.plugins;
  exports it.jnrpe.engine.services.auth;
  exports it.jnrpe.engine.events;
  exports it.jnrpe.engine.plugins;
  exports it.jnrpe.engine.commands;

  uses IConfigProvider;
  uses ICommandRepository;
  uses IPluginRepository;
  uses IEventManager;
  uses it.jnrpe.engine.services.auth.IAuthService;

  provides ICommandRepository with
      EmbeddedCommandsRepository;
  provides IAuthService with
      SimpleAuthProvider;
}
