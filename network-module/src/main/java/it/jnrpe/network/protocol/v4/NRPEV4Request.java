package it.jnrpe.network.protocol.v4;

public class NRPEV4Request extends NRPEV4AbstractPacket {
    public NRPEV4Request(long crc32, int alignment, byte[] buffer, byte[] padding) {
        super(1, crc32, 0, alignment, buffer, padding);
    }
}
