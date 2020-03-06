package it.jnrpe.network.protocol.v3;

public class NRPEV3Request extends NRPEV3AbstractPacket {
    public NRPEV3Request(long crc32, int alignment, byte[] buffer, byte[] padding) {
        super(1, crc32, 0, alignment, buffer, padding);
    }
}
