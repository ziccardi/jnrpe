import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.services.config.yaml.tests.YamlConfigSource;

open module jnrpe.it.jnrpe.services.config.test {
  requires it.jnrpe.engine;
  requires org.junit.jupiter.api;

  provides IConfigSource with
      YamlConfigSource;

  uses IConfigProvider;
  uses IConfigSource;
}
