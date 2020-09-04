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
package it.jnrpe.network;

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import it.jnrpe.command.execution.ExecutionResult;
import it.jnrpe.command.execution.ICommandExecutor;
import it.jnrpe.network.protocol.ProtocolPacket;
import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
class ServerHandler extends ChannelInboundHandlerAdapter {
  private List<ICommandExecutor> commandHandlers = new ArrayList<>();

  ServerHandler() {}

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    try {
      ProtocolPacket packet = (ProtocolPacket) msg;

      ctx.channel()
          .attr(AttributeKey.<Integer>valueOf("version"))
          .set(((ProtocolPacket) msg).getVersion());

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
