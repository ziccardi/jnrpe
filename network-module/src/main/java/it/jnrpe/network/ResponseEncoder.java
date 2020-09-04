package it.jnrpe.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import it.jnrpe.command.execution.ExecutionResult;
import it.jnrpe.network.encoders.EncoderFactory;

public class ResponseEncoder extends MessageToByteEncoder<ExecutionResult> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ExecutionResult result, ByteBuf out) throws Exception {
        int version = ctx.channel().attr(AttributeKey.<Integer>valueOf("version")).get();
        out.writeBytes(EncoderFactory.produceEncoder(version, result).encode());
    }
}
