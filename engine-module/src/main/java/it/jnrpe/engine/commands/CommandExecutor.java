package it.jnrpe.engine.commands;

import it.jnrpe.command.execution.ExecutionResult;
import it.jnrpe.command.execution.ICommandExecutor;
import it.jnrpe.command.execution.Status;
import it.jnrpe.engine.plugins.PluginRepository;

public class CommandExecutor implements ICommandExecutor {
    @Override
    public ExecutionResult execute(String cmd) {
        System.out.println("Received command: " + cmd);
        System.out.println("Plugins: " + new PluginRepository().getAllPlugins());
        return new ExecutionResult("Command executed", Status.OK);
    }
}
