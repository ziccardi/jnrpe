package it.jnrpe.network.decoders;

import io.netty.buffer.ByteBuf;
import it.jnrpe.network.protocol.ProtocolPacket;

public interface IPacketBuilder {
    IPacketBuilder withByteBuf(final ByteBuf buffer);
    ProtocolPacket build();
}
