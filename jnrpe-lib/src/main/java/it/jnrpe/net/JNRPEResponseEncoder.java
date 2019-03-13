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

/**
 * This object is inserted inside the NETTY pipeline to create serialize a
 * {@link JNRPEResponse} object.
 * 
 * @author Massimiliano Ziccardi
 *
 * @version $Revision: 1.0 $
 */
public class JNRPEResponseEncoder extends MessageToByteEncoder<JNRPEResponse> {

    /**
     * Constructor.
     */
    public JNRPEResponseEncoder() {
    }

    /**
     * Method encode.
     * See {@link MessageToByteEncoder#encode(ChannelHandlerContext, Object, ByteBuf)}
     * @param ctx ChannelHandlerContext
     * @param msg JNRPEResponse
     * @param out ByteBuf
     */
    @Override
    protected final void encode(final ChannelHandlerContext ctx, final JNRPEResponse msg, final ByteBuf out) {
        out.writeBytes(msg.toByteArray());
    }

}
