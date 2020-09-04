import it.jnrpe.command.execution.ICommandExecutor;
import it.jnrpe.engine.commands.CommandExecutor;
import it.jnrpe.plugin.service.IPlugin;

module jnrpe.engine.module {
    requires jnrpe.plugin.service;
    requires jnrpe.network.module;
    requires command.executor.service;

    uses IPlugin;
    provides ICommandExecutor with CommandExecutor;
}
