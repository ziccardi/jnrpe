package it.jnrpe.server;

import it.jnrpe.engine.commands.CommandExecutor;
import it.jnrpe.network.ServerListener;

public class Server {
    public static void main(String[] args) {
        new ServerListener().withCommandHandler(new CommandExecutor()).listen(5666);
    }
}
