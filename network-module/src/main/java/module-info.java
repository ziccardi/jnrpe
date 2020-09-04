import it.jnrpe.command.execution.ICommandExecutor;

module jnrpe.network.module {
  exports it.jnrpe.network;

  requires io.netty.all;
  requires command.executor.service;

  uses ICommandExecutor;
}
