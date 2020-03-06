package it.jnrpe.network.execution;

@FunctionalInterface
public interface ICommandExecutor {
    public ExecutionResult execute(String cmd);
}
