/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
package it.jnrpe.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JNRPEResponseEncoder extends MessageToByteEncoder<JNRPEResponse> {

	public JNRPEResponseEncoder() {
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, JNRPEResponse msg,
			ByteBuf out) throws Exception {
		msg.updateCRC();
		out.writeShort(msg.getPacketVersion().intValue());
		out.writeShort(msg.getPacketType().intValue());
		out.writeInt(msg.getCRC());
		out.writeShort(msg.getResultCode());
		out.writeBytes(msg.getBuffer());
		out.writeBytes(msg.getDummy());
	}

}
