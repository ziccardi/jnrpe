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

/**
 * The NETTY implementation of the JNRPE protocol request decoder.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class JNRPERequestDecoder extends ReplayingDecoder<JNRPERequestDecoder.STAGE> {

    /**
     * The packet buffer length in bytes.
     */
    private static final int PACKETBUFFER_LENGTH = 1024;

    /**
     * The size of the random byte array.
     */
    private static final int DUMMYLENGTH = 2;

    /**
     * The decoded protocol packet.
     */
    private JNRPEProtocolPacket packet;

    /**
     * The decoded packet version.
     */
    private PacketVersion packetVersion;

    /**
     * The NETTY {@link ReplayingDecoder} will be called many times until all
     * the data has been received from the server. This enum will be used to
     * store the decoding process progress.
     * 
     * @author Massimiliano Ziccardi
     */
    protected enum STAGE {
        /**
         * The next data we have to receive is the PACKET_VERSION.
         */
        PACKET_VERSION,
        /**
         * The next data we have to receive is the REQUST TYPE CODE.
         */
        PACKET_TYPE_CODE,
        /**
         * The next data we have to receive is the request CRC.
         */
        CRC,
        /**
         * The next data we have to receive is the RESULT CODE.
         */
        RESULT_CODE,
        /**
         * The next data we have to receive is DATA BUFFER.
         */
        BUFFER,
        /**
         * The next data we have to receive is DUMMY buffer.
         */
        DUMMY
    };

    /**
     * Creates a new {@link JNRPERequestDecoder} object and sets the initial
     * state at {@link STAGE#PACKET_VERSION}.
     */
    public JNRPERequestDecoder() {
        super(STAGE.PACKET_VERSION);
    }

    /**
     * Method decode.
     * @param ctx ChannelHandlerContext
     * @param in ByteBuf
     * @param out List<Object>
     * @throws Exception
     */
    @Override
    protected final void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        
        STAGE stage = state();
        
        switch (stage) {
        case PACKET_VERSION:
            packetVersion = PacketVersion.fromIntValue(in.readShort());
            checkpoint(STAGE.PACKET_TYPE_CODE);
        case PACKET_TYPE_CODE:
            PacketType type = PacketType.fromIntValue(in.readShort());
            switch (type) {
            case QUERY:
                packet = new JNRPERequest();
                break;
            case RESPONSE:
                packet = new JNRPEResponse();
                break;
            default:
                throw new Exception("Unknown packet type: " + stage);
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

    /**
     * Resets the decoder to the initial state.
     */
    private void reset() {
        checkpoint(STAGE.PACKET_VERSION);
    }

    /**
     * Convert a '0' terminated string to a java string.
     * 
     * @param buff
     *            the '0' terminated string
    
     * @return the java string */
    private String ztString2String(final byte[] buff) {
        return new String(buff, 0, ArrayUtils.indexOf(buff, (byte) 0));
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "JNRPERequestDecoder [packet=" + packet + ", packetVersion=" + packetVersion + "]";
    }

}
