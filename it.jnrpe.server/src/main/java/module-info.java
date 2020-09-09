import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.server.ConfigSource;

module it.jnrpe.server {
  requires it.jnrpe.engine;
  requires it.jnrpe.services.network.netty;
  requires it.jnrpe.services.config;

  provides IConfigSource with
      ConfigSource;

  uses INetworkListener;
  uses IConfigProvider;
}
