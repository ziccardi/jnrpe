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

import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import it.jnrpe.engine.services.commands.CommandExecutor;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.services.network.netty.protocol.ProtocolPacket;
import java.util.Arrays;

@ChannelHandler.Sharable
class ServerHandler extends ChannelInboundHandlerAdapter {
  private CommandExecutor commandExecutor = new CommandExecutor();

  ServerHandler() {}

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    try {
      ProtocolPacket packet = (ProtocolPacket) msg;

      ctx.channel()
          .attr(AttributeKey.<Integer>valueOf("version"))
          .set(((ProtocolPacket) msg).getVersion());

      // A command received by check_nrpe is composed like this:
      // command!param1!param2!...!paramN
      String[] commandParts = packet.getCommand().split("!");
      String[] params =
          commandParts.length > 1
              ? Arrays.copyOfRange(commandParts, 1, commandParts.length)
              : new String[] {};

      ExecutionResult res = this.commandExecutor.execute(commandParts[0], params);

      ChannelFuture channelFuture = ctx.writeAndFlush(res);
      channelFuture.addListener(ChannelFutureListener.CLOSE);
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }
}
