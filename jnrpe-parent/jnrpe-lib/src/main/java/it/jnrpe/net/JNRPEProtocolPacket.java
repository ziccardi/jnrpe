/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
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
package it.jnrpe.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * This class represent a generic NRPE protocol packet.
 *
 * @author Massimiliano Ziccardi
 */
class JNRPEProtocolPacket {
    /**
     * Max amount of data we'll send in one query/response.
     */
    private static final int MAX_PACKETBUFFER_LENGTH = 1024;

    /**
     * The CRC value.
     */
    private int crcValue = 0;

    /**
     * The packet type.
     */
    private int packetTypeCode = 0;

    /**
     * The packet version.
     */
    private int packetVersion = 0;

    /**
     * The result code.
     */
    private int resultCode = 0;

    /**
     * The packet buffer.
     */
    private byte[] byteBufferAry = new byte[MAX_PACKETBUFFER_LENGTH];

    /**
     * Dummy bytes.
     */
    private byte[] dummyBytesAry = new byte[2];

    /**
     * Returns the packet CRC value.
     *
     * @return the CRC value
     */
    public int getCRC() {
        return crcValue;
    }

    /**
     * Returns the packet type.
     *
     * @return The packet type
     */
    public PacketType getPacketType() {
        return PacketType.fromIntValue(packetTypeCode);
    }

    /**
     * Returns the packet version.
     *
     * @return The packet version
     */
    public PacketVersion getPacketVersion() {
        return PacketVersion.fromIntValue(packetVersion);
    }

    /**
     * Sets the CRC value.
     *
     * @param crc
     *            The new CRC value
     */
    public void setCRC(final int crc) {
        crcValue = crc;
    }

    /**
     * Sets the packet type.
     *
     * @param packetType
     *            The new packet type
     */
    protected void setPacketType(final PacketType packetType) {
        packetTypeCode = packetType.intValue();
    }

    /**
     * Sets the packet version.
     *
     * @param version
     *            The packet version
     */
    public void setPacketVersion(final PacketVersion version) {
        packetVersion = version.intValue();
    }

    /**
     * Returns the result code.
     *
     * @return The result code
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the result code.
     *
     * @param status
     *            The new result code
     */
    public void setResultCode(final int status) {
        resultCode = status;
    }

    /**
     * Initialize the object reading the data from the input stream.
     *
     * @param in
     *            The stream to be read
     * @throws IOException
     *             On any I/O error
     */
    protected void fromInputStream(final InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        packetVersion = din.readShort();
        packetTypeCode = din.readShort();
        crcValue = din.readInt();
        resultCode = din.readShort();
        din.readFully(byteBufferAry);
        din.readFully(dummyBytesAry);
    }

    /**
     * Validates the packet CRC.
     *
     * @throws BadCRCException
     *             If the CRC can't be validated
     */
    public void validate() throws BadCRCException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(packetVersion);
            dout.writeShort(packetTypeCode);
            dout.writeInt(0); // NO CRC
            dout.writeShort(resultCode);
            dout.write(byteBufferAry);
            dout.write(dummyBytesAry);

            dout.close();

            byte[] vBytes = bout.toByteArray();

            CRC32 crcAlg = new CRC32();
            crcAlg.update(vBytes);

            if (!(((int) crcAlg.getValue()) == crcValue)) {
                throw new BadCRCException("Bad CRC");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts the packet object to its byte array representation.
     *
     * @return The byte array representation of this packet.
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(packetVersion);
            dout.writeShort(packetTypeCode);
            dout.writeInt(crcValue);
            dout.writeShort(resultCode);
            dout.write(byteBufferAry);
            dout.write(dummyBytesAry);

            dout.close();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return bout.toByteArray();
    }

    /**
     * Returns the string message.
     *
     * @return The string message
     */
    public String getStringMessage() {
        int zeroIndex = MAX_PACKETBUFFER_LENGTH - 1;

        // find the first 0 byte
        for (int i = 0; i < MAX_PACKETBUFFER_LENGTH; i++) {
            if (byteBufferAry[i] == 0) {
                zeroIndex = i;
                break;
            }
        }

        return new String(byteBufferAry, 0, zeroIndex);
    }

    /**
     * Sets the packet message. If the message is longer than.
     * {@link JNRPEProtocolPacket#MAX_PACKETBUFFER_LENGTH} than it gets
     * truncated to {@link JNRPEProtocolPacket#MAX_PACKETBUFFER_LENGTH} bytes.
     *
     * @param message
     *            The message
     */
    protected void _setMessage(final String message) {
        if (message == null) {
            byteBufferAry[0] = 0;
            return;
        }
        System.arraycopy(message.getBytes(), 0, byteBufferAry, 0,
                Math.min(message.length(), MAX_PACKETBUFFER_LENGTH));

        if (message.length() < MAX_PACKETBUFFER_LENGTH) {
            byteBufferAry[message.length()] = 0;
        }
    }

    /**
     * Initializes the arrays with random data. Not sure it is really needed...
     */
    protected void initRandomBuffer() {
        Random r = new Random(System.currentTimeMillis());

        r.nextBytes(byteBufferAry);
        r.nextBytes(dummyBytesAry);
    }

    /**
     * Write the command name inside the JNRPE packet.
     *
     * @param commandName
     *            The command name
     */
    protected void setDataBuffer(final String commandName) {
        if (commandName == null) {
            throw new IllegalArgumentException("Command name can't be null");
        }

        byteBufferAry =
                Arrays.copyOf(commandName.getBytes(), MAX_PACKETBUFFER_LENGTH);
    }
}
