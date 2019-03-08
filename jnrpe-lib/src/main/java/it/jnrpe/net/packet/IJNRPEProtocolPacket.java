package it.jnrpe.net.packet;

import it.jnrpe.net.PacketType;

public interface IJNRPEProtocolPacket {
    void setType(final PacketType packetType);
    void setBuffer(final String buffer);
    String getBufferAsString();
    void updateCRC();
    int getType();
    int getVersion();
    int getResultCode();
    byte[] toByteArray();
    boolean isValid();
    void setResultCode(int code);
}
