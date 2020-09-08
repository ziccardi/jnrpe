import it.jnrpe.engine.services.network.INetworkListener;

module it.jnrpe.server {
    requires it.jnrpe.engine;
    requires it.jnrpe.services.network.netty;

    uses INetworkListener;
}
