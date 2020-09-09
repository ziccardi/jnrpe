/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.services.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import it.jnrpe.engine.services.config.Binding;
import it.jnrpe.engine.services.network.INetworkListener;

public class JnrpeNettyListenerService implements INetworkListener {
  private final ServerHandler serverHandler = new ServerHandler();

  @Override
  public String getName() {
    return "Netty Listener Service";
  }

  public void bind(Binding binding) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    final ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(
            new ChannelInitializer<>() {
              @Override
              protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new RequestDecoder(), new ResponseEncoder(), serverHandler);
              }
            })
        .option(ChannelOption.SO_BACKLOG, 50)
        .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

    serverBootstrap.bind(binding.getPort());
    System.out.println("Started listening on port " + binding.getPort());
  }

  @Override
  public boolean supportBinding(Binding binding) {
    return !binding.isSsl();
  }
}
