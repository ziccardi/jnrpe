package it.jnrpe.net.packet;

import io.netty.buffer.ByteBuf;
import it.jnrpe.net.packet.v2.JNRPEProtocolPacketV2;
import it.jnrpe.net.packet.v3.JNRPEProtocolPacketV3;

import java.io.IOException;
import java.io.InputStream;

public class JNRPEProtocolPacketFactory {

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
        return new JNRPEProtocolPacketV3(); // FIXME: allow to configure the version to be created
    }
}
