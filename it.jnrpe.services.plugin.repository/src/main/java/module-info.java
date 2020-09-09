import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.engine.services.plugins.IPluginRepository;
import it.jnrpe.services.plugin.JavaServicePluginRepository;

module it.jnrpe.services.plugin.repository {
  requires it.jnrpe.engine;

  provides IPluginRepository with
      JavaServicePluginRepository;

  uses IPlugin;
}
