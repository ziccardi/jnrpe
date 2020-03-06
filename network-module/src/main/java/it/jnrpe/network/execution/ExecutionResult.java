package it.jnrpe.network.execution;

public class ExecutionResult {
    private final String message;
    private final Status status;

    public ExecutionResult(final String message, final Status status) {
        this.message = message;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
