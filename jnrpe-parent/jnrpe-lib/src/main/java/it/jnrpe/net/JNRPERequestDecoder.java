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
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class JNRPERequestDecoder extends
		ReplayingDecoder<JNRPERequestDecoder.STAGE> {

	private static final int PACKETBUFFER_LENGTH = 1024;
	private static final int DUMMYLENGTH = 2;

	private JNRPEProtocolPacket packet;

	private PacketVersion packetVersion;

	public enum STAGE {
		PACKET_VERSION, PACKET_TYPE_CODE, CRC, RESULT_CODE, BUFFER, DUMMY
	};

	public JNRPERequestDecoder() {
		super(STAGE.PACKET_VERSION);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {

		switch (state()) {
		case PACKET_VERSION:
			// packet.setPacketVersion(PacketVersion.fromIntValue(in.readShort()));
			packetVersion = PacketVersion.fromIntValue(in.readShort());
			checkpoint(STAGE.PACKET_TYPE_CODE);
		case PACKET_TYPE_CODE:
			// packet.setPacketType(PacketType.fromIntValue(in.readShort()));
			PacketType type = PacketType.fromIntValue(in.readShort());
			switch (type) {
			case QUERY:
				packet = new JNRPERequest();
				break;
			case RESPONSE:
				packet = new JNRPEResponse();
				break;
			default:
				throw new Exception("Unknown packet type");
			}

			packet.setPacketVersion(packetVersion);
			checkpoint(STAGE.CRC);
		case CRC:
			packet.setCRC(in.readInt());
			checkpoint(STAGE.RESULT_CODE);
		case RESULT_CODE:
			packet.setResultCode(in.readShort());
			checkpoint(STAGE.BUFFER);
		case BUFFER:
			byte[] buff = new byte[PACKETBUFFER_LENGTH];
			in.readBytes(buff);
			packet.setBuffer(ztString2String(buff));
			checkpoint(STAGE.DUMMY);
		case DUMMY:
			byte[] dummy = new byte[DUMMYLENGTH];
			packet.setDummy(dummy);
			out.add(packet);
			reset();
			break;
		default:
			throw new Error("Shouldn't reach here.");
		}
	}

	private void reset() {
		checkpoint(STAGE.PACKET_VERSION);
	}

	private String ztString2String(byte[] buff) {
		return new String(buff, 0, ArrayUtils.indexOf(buff, (byte) 0));
	}

}
