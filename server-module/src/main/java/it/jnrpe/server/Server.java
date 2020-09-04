package it.jnrpe.server;

import it.jnrpe.network.ServerListener;

public class Server {
    public static void main(String[] args) {
        new ServerListener().listen(5666);
    }
}
