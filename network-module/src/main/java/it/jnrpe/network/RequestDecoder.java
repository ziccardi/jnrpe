package it.jnrpe.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import it.jnrpe.network.decoders.DecoderBuilder;
import it.jnrpe.network.protocol.ProtocolPacket;

import java.util.List;

class RequestDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int protocolVersion = in.readUnsignedShort();
        ProtocolPacket packet = DecoderBuilder.forPacket(protocolVersion, 1).withByteBuf(in).build();
        out.add(packet);
    }
}
