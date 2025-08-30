import it.jnrpe.engine.services.auth.tests.YamlConfigSource;
import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;

open module it.jnrpe.integration.test {
  uses it.jnrpe.engine.services.network.INetworkListener;

  requires it.jnrpe.engine;
  requires it.jnrpe.services.config;
  requires it.jnrpe.services.network.netty;
  requires testcontainers;
  requires org.junit.jupiter.api;
  requires org.junit.jupiter.engine;
  requires org.junit.jupiter.params;
  requires org.junit.platform.launcher;
  requires org.hamcrest;
  requires com.fasterxml.jackson.annotation;
  requires org.slf4j;

  provides IConfigSource with YamlConfigSource;

  uses IConfigProvider;
  uses IConfigSource;
}
