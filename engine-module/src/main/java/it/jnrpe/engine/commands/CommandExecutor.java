package it.jnrpe.engine.commands;

import it.jnrpe.engine.plugins.PluginRepository;
import it.jnrpe.network.execution.ExecutionResult;
import it.jnrpe.network.execution.ICommandExecutor;
import it.jnrpe.network.execution.Status;

public class CommandExecutor implements ICommandExecutor {
    @Override
    public ExecutionResult execute(String cmd) {
        System.out.println("Received command: " + cmd);
        System.out.println("Plugins: " + new PluginRepository().getAllPlugins());
        return new ExecutionResult("Command executed", Status.OK);
    }
}
