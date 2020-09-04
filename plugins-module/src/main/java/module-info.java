import it.jnrpe.plugin.service.IPlugin;
import it.jnrpe.plugins.HelloWorldPlugin;

module jnrpe.plugins.module.main {
  requires jnrpe.plugin.service;

  provides IPlugin with
      HelloWorldPlugin;
}
