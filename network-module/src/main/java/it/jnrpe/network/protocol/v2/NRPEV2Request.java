package it.jnrpe.network.protocol.v2;

public class NRPEV2Request extends NRPEV2AbstractPacket {
    public NRPEV2Request(long crc32, int resultCode, byte[] buffer, byte[] padding) {
        super(1, crc32, resultCode, buffer, padding);
    }
}
