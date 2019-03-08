/*******************************************************************************
 * Copyright (c) 2007, 2019 Massimiliano Ziccardi
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
package it.jnrpe.net.packet;

import io.netty.buffer.ByteBuf;
import it.jnrpe.net.packet.v2.JNRPEProtocolPacketV2;
import it.jnrpe.net.packet.v3.JNRPEProtocolPacketV3;

import java.io.IOException;
import java.io.InputStream;

public class JNRPEProtocolPacketFactory {

    public static int PROTOCOL_VERSION = 3;

    private static IJNRPEProtocolPacket produce(DataStreamAdapter in) throws IOException {
        int version = in.readUnisgnedInt16();
        switch (version) {
            case 2:
                return new JNRPEProtocolPacketV2().load(in);
            case 3:
                return new JNRPEProtocolPacketV3().load(in);
            default:
                throw new IllegalStateException("Unknown protocol"); // FIXME: throw a JNRPE Exception
        }
    }

    public static IJNRPEProtocolPacket produce(InputStream in) throws IOException {
        return produce(DataStreamAdapter.forData(in));
    }

    public static IJNRPEProtocolPacket produce(ByteBuf in) throws IOException {
        return produce(DataStreamAdapter.forData(in));
    }

    public static IJNRPEProtocolPacket createNew() {
        return createNew(PROTOCOL_VERSION);
    }

    public static IJNRPEProtocolPacket createNew(int version) {
        switch (version) {
            case 2:
                return new JNRPEProtocolPacketV2();
            case 3:
                return new JNRPEProtocolPacketV3();
            default:
                throw new IllegalStateException("Unknown protocol"); // FIXME: throw a JNRPE Exception
        }
    }
}
