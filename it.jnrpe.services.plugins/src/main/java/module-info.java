import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.services.plugins.CheckDiskPlugin;
import it.jnrpe.services.plugins.CheckTestPlugin;

module jnrpe.it.jnrpe.services.plugins {
  requires it.jnrpe.engine;

  opens it.jnrpe.services.plugins to
      it.jnrpe.engine;

  provides IPlugin with
      CheckDiskPlugin,
      CheckTestPlugin;
}
