package it.jnrpe.network.protocol.v4;

import it.jnrpe.network.protocol.ProtocolPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

abstract class NRPEV4AbstractPacket extends ProtocolPacket {
    public NRPEV4AbstractPacket(int type, long crc32, int resultCode, int alignment, byte[] buffer, byte[] padding) {
        super(4, type, crc32, resultCode, alignment, buffer, padding);
    }

    protected long crc32() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(this.getVersion());
            dout.writeShort(this.getPacketType());
            dout.writeInt(0); // NO CRC
            dout.writeShort(0); // No result code
            dout.writeShort(this.getAlignment());
            dout.writeInt(this.getBuffer().length);
            dout.write(this.getBuffer());
            dout.write(this.getPadding());
            dout.close();

            byte[] bytes = bout.toByteArray();
            CRC32 crcAlg = new CRC32();
            crcAlg.update(bytes);

            return crcAlg.getValue();
        } catch (IOException e) {
            // Never happens...
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
