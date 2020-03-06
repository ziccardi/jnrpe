package it.jnrpe.network.decoders;

import io.netty.buffer.ByteBuf;
import it.jnrpe.network.protocol.ProtocolPacket;

abstract class AbstractPacketBuilder implements IPacketBuilder {
    protected ByteBuf buffer;

    private long crc32;
    private int resultCode;
    private byte[] requestBuffer;
    private byte[] padding;
    private int alignment;

    public IPacketBuilder withByteBuf(final ByteBuf buffer) {
        this.buffer = buffer;
        return this;
    }

    @Override
    public final ProtocolPacket build() {
        int packetType = this.buffer.readUnsignedShort(); // must be 1 for requests
        if (packetType != 1) {
            // FIXME: throw exception
        }
        this.loadCommonData();
        this.loadRequestFromBuffer();
        return this.buildPacket();
    }

    private void loadCommonData() {
        this.crc32 = this.buffer.readUnsignedInt();
        this.resultCode = this.buffer.readUnsignedShort(); // empty for requests
    }

    protected long getCrc32() {
        return crc32;
    }

    protected int getResultCode() {
        return resultCode;
    }

    protected byte[] getRequestBuffer() {
        return requestBuffer;
    }

    protected void setRequestBuffer(byte[] requestBuffer) {
        this.requestBuffer = requestBuffer;
    }

    protected byte[] getPadding() {
        return padding;
    }

    protected void setPadding(byte[] padding) {
        this.padding = padding;
    }

    protected int getAlignment() {
        return alignment;
    }

    protected void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    protected abstract void loadRequestFromBuffer();
    protected abstract ProtocolPacket buildPacket();
}
