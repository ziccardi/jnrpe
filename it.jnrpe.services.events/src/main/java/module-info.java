import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.services.events.console.ConsoleEventManager;

module jnrpe.it.jnrpe.services.events.main {
  requires it.jnrpe.engine;

  provides IEventManager with
      ConsoleEventManager;
}
