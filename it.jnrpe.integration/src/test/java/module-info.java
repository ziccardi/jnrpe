import it.jnrpe.engine.services.auth.tests.YamlConfigSource;
import it.jnrpe.engine.services.config.IConfigSource;

open module it.jnrpe.integration.test {
  uses it.jnrpe.engine.services.network.INetworkListener;

  requires it.jnrpe.engine;
  requires it.jnrpe.services.config;
  requires it.jnrpe.services.network.netty;
  requires org.junit.jupiter.api;

  provides IConfigSource with
      YamlConfigSource;
}
