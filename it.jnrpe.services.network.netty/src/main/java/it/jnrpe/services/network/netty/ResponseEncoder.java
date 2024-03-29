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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.services.network.netty.encoders.Encoder;

public class ResponseEncoder extends MessageToByteEncoder<ExecutionResult> {
  @Override
  protected void encode(ChannelHandlerContext ctx, ExecutionResult result, ByteBuf out)
      throws Exception {
    out.writeBytes(
        Encoder.forVersion(ctx.channel().attr(AttributeKey.<Integer>valueOf("version")).get())
            .andResult(result)
            .encode());
  }
}
