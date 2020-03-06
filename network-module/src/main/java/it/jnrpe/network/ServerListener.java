package it.jnrpe.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import it.jnrpe.network.execution.ICommandExecutor;

public class ServerListener {

    private final ServerHandler serverHandler = new ServerHandler();

    public ServerListener listen(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new RequestDecoder(), new ResponseEncoder(),serverHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 50)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        serverBootstrap.bind(port);
        System.out.println("Started");
        return this;
    }

    public ServerListener withCommandHandler(ICommandExecutor commandHandler) {
        this.serverHandler.addCommandHandler(commandHandler);
        return this;
    }

    public static void main(String[] args) {
        new ServerListener().listen(5666);
    }
}
