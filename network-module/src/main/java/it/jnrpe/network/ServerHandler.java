package it.jnrpe.network;

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import it.jnrpe.network.execution.ExecutionResult;
import it.jnrpe.network.execution.ICommandExecutor;
import it.jnrpe.network.protocol.ProtocolPacket;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
class ServerHandler extends ChannelInboundHandlerAdapter {
    private List<ICommandExecutor> commandHandlers = new ArrayList<>();

    ServerHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ProtocolPacket packet = (ProtocolPacket) msg;

            ctx.channel().attr(AttributeKey.<Integer>valueOf("version")).set(((ProtocolPacket) msg).getVersion());

            ExecutionResult res = commandHandlers.get(0).execute(packet.getCommand());

            ChannelFuture channelFuture = ctx.writeAndFlush(res);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void addCommandHandler(final ICommandExecutor commandHandler) {
        this.commandHandlers.add(commandHandler);
    }
}
