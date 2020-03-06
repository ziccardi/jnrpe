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
        ProtocolPacket packet = DecoderBuilder.forPacket(in.readUnsignedShort(), 1).withByteBuf(in).build();
        System.out.println("Valid: " + packet.validateCRC());
        out.add(packet);
    }
}
