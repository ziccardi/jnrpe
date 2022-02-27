import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.services.network.netty.JnrpeNettyListenerService;

module it.jnrpe.services.network.netty {
  requires it.jnrpe.engine;
  requires io.netty.all;
  requires org.bouncycastle.provider;

  provides INetworkListener with
      JnrpeNettyListenerService;
}
