import it.jnrpe.engine.services.commands.ICommandRepository;
import it.jnrpe.services.command.repository.ConfigCommandRepository;

module it.jnrpe.services.command.repository {
  requires it.jnrpe.engine;

  provides ICommandRepository with
      ConfigCommandRepository;
}
