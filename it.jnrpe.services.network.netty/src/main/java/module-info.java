import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.services.network.netty.JnrpeNettyListenerService;

module it.jnrpe.services.network.netty {
  requires it.jnrpe.engine;
  requires org.bouncycastle.provider;
  requires io.netty.handler;
  requires io.netty.buffer;
  requires io.netty.transport;
  requires io.netty.codec;
  requires io.netty.common;

  provides INetworkListener with
      JnrpeNettyListenerService;
}
