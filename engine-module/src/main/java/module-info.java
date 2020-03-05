import it.jnrpe.plugin.service.IPlugin;

module jnrpe.engine.module.main {
    requires jnrpe.plugin.service;

    uses IPlugin;
}