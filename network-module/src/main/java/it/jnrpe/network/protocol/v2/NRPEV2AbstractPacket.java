package it.jnrpe.network.protocol.v2;

import it.jnrpe.network.protocol.ProtocolPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

abstract class NRPEV2AbstractPacket extends ProtocolPacket {

    public NRPEV2AbstractPacket(int type, long crc32, int resultCode, byte[] buffer, byte[] padding) {
        super(2, type, crc32, resultCode, 0, buffer, padding);
    }

    protected long crc32() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(this.getVersion());
            dout.writeShort(this.getPacketType());
            dout.writeInt(0); // NO CRC
            dout.writeShort(this.getResultCode());
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
