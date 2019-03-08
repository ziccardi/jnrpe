package it.jnrpe.net.packet.v3;

import it.jnrpe.net.packet.AbstractJNRPEProtocolPacket;
import it.jnrpe.net.packet.DataStreamAdapter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class JNRPEProtocolPacketV3 extends AbstractJNRPEProtocolPacket {
    private int alignment;
    private int bufferLength = 0;
    private byte[] buffer = new byte[0];

    private byte[] dummy = new byte[3];

    private final static int MIN_BUFFER_LEN = 1033;

    public JNRPEProtocolPacketV3() {
        super(3);
    }

    /**
     * Sets the value of the data buffer.
     *
     * @param buffer the buffer value
     */
    public void setBuffer(final String buffer) {
        this.buffer = Arrays.copyOf(buffer.getBytes(), Math.max(buffer.length() + 1, MIN_BUFFER_LEN));
        this.bufferLength = this.buffer.length;
    }

    @Override
    public byte[] getBuffer() {
        return this.buffer;
    }

    protected void finishLoading(final DataStreamAdapter in) throws IOException {
        this.alignment = in.readUnisgnedInt16();
        this.bufferLength = in.readUnisgnedInt32();
        this.buffer = new byte[this.bufferLength];
        in.readFully(buffer);
        in.readFully(this.dummy);
    }

    @Override
    protected void finishWriting(DataOutputStream dout) throws IOException {
        dout.writeShort(0);
        dout.writeInt(buffer.length);
        dout.write(buffer);
        dout.write(dummy);
    }
}
