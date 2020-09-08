package it.jnrpe.server;

import it.jnrpe.engine.services.network.INetworkListener;

import java.util.Optional;
import java.util.ServiceLoader;

public class JNRPEServer {
    public static void main(String[] args) {
        // retrieve the network service
        ServiceLoader<INetworkListener> serviceLoader = ServiceLoader.load(INetworkListener.class);
        Optional<INetworkListener> listener = serviceLoader.findFirst();
        if (listener.isPresent()) {
            listener.get().listen(5666);
        } else {
            System.out.println("No network service found");
        }
    }
}
