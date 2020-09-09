import it.jnrpe.engine.services.config.IConfigProvider;

module it.jnrpe.engine {
  exports it.jnrpe.engine.services.commands;
  exports it.jnrpe.engine.services.config;
  exports it.jnrpe.engine.services.events;
  exports it.jnrpe.engine.services.network;
  exports it.jnrpe.engine.services.plugins;

  uses IConfigProvider;
}
