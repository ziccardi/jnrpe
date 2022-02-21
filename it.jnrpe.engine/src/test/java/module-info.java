import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;

open module it.jnrpe.engine.test {
  requires it.jnrpe.engine;
  requires org.junit.jupiter.api;

  uses IConfigProvider;
  uses IConfigSource;
}
