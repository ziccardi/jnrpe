package it.jnrpe.command.execution;

@FunctionalInterface
public interface ICommandExecutor {
    public ExecutionResult execute(String cmd);
}
