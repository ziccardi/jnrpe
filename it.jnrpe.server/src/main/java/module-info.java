import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.server.ConfigSource;

module it.jnrpe.server {
  opens it.jnrpe.server to
      it.jnrpe.engine;
  opens it.jnrpe.server.commands to
      it.jnrpe.engine;

  requires it.jnrpe.engine;

  provides IConfigSource with
      ConfigSource;

  uses INetworkListener;
  uses IConfigProvider;
}
