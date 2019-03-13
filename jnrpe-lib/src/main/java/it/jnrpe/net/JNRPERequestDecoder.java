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
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import it.jnrpe.net.packet.IJNRPEProtocolPacket;
import it.jnrpe.net.packet.JNRPEProtocolPacketFactory;
import org.apache.commons.lang.ArrayUtils;

/**
 * The NETTY implementation of the JNRPE protocol request decoder.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class JNRPERequestDecoder extends ReplayingDecoder<Void> {

    /**
     * Creates a new {@link JNRPERequestDecoder} object
     */
    public JNRPERequestDecoder() {
    }

    /**
     * Netty decode method. @see {@link ReplayingDecoder#decode(ChannelHandlerContext, ByteBuf, List)}
     *
     * @param ctx teh context
     * @param in data to be decoded
     * @param out decoded data
     * @throws Exception thrown in case of unknown packet received
     */
    @Override
    protected final void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        out.add(new JNRPERequest(JNRPEProtocolPacketFactory.produce(in)));
    }
}
