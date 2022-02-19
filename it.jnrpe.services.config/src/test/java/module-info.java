import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.services.config.yaml.tests.YamlConfigSource;

open module it.jnrpe.services.config.test {
  requires it.jnrpe.engine;
  requires org.junit.jupiter.api;
  requires it.jnrpe.services.config;
  requires org.yaml.snakeyaml;

  provides IConfigSource with
      YamlConfigSource;

  uses IConfigProvider;
  uses IConfigSource;
}
