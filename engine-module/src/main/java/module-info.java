import it.jnrpe.plugin.service.IPlugin;

module jnrpe.engine.module {
    requires jnrpe.plugin.service;
    requires jnrpe.network.module;

    exports it.jnrpe.engine.commands;

    uses IPlugin;
}