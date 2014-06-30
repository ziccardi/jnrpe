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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import org.apache.commons.lang.ArrayUtils;

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

    // FIXME : charset from configuration...
    /**
     * The charset.
     */
    private Charset charset = Charset.defaultCharset();

    /**
     * The CRC value.
     */
    private int crcValue;

    /**
     * The packet type.
     */
    private int packetTypeCode;

    /**
     * The packet version.
     */
    private int packetVersion;

    /**
     * The result code.
     */
    private int resultCode;

    /**
     * The packet buffer.
     */
    private byte[] byteBufferAry = new byte[MAX_PACKETBUFFER_LENGTH];

    /**
     * Dummy bytes.
     */
    private final byte[] dummyBytesAry = new byte[2];

    /**
     * Returns the packet CRC value.
     * 
     * @return the CRC value
     */
    int getCRC() {
        return crcValue;
    }

    /**
     * Returns the packet type.
     * 
     * @return The packet type
     */
    PacketType getPacketType() {
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
    void setCRC(final int crc) {
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
     * Changes the default charset.
     * 
     * @param newCharset
     *            the new charset,
     */
    public void setCharset(final Charset newCharset) {
        this.charset = newCharset;
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
     * @return the received raw data buffer
     */
    protected byte[] getBuffer() {
        return byteBufferAry;
    }

    /**
     * @return The string representation of the buffer.
     */
    protected String getPacketString() {
        byte[] buffer = getBuffer();
        int zeroIndex = ArrayUtils.indexOf(buffer, (byte) 0);

        if (zeroIndex == ArrayUtils.INDEX_NOT_FOUND) {
            return new String(buffer, charset);
        } else {
            return new String(buffer, 0, zeroIndex, charset);
        }
    }

    /**
     * Initializes the arrays with random data. Not sure it is really needed...
     */
    private void initRandomBuffer() {
        Random r = new Random(System.currentTimeMillis());

        r.nextBytes(byteBufferAry);
        r.nextBytes(dummyBytesAry);
    }

    /**
     * Sets the value of the data buffer.
     * 
     * @param buffer the buffer value
     */
    protected void setBuffer(final String buffer) {
        initRandomBuffer();
        byteBufferAry = Arrays.copyOf(buffer.getBytes(charset), MAX_PACKETBUFFER_LENGTH);
    }

    /**
     * Sets the dummy buffer value.
     * 
     * @param dummyBytes the new value
     */
    void setDummy(final byte[] dummyBytes) {
        if (dummyBytes == null || dummyBytes.length != 2) {
            throw new IllegalArgumentException("Dummy bytes array must have exactly two elements");
        }

        System.arraycopy(dummyBytes, 0, this.dummyBytesAry, 0, 2);
    }

    /**
     * @return the dummy buffer value.
     */
    byte[] getDummy() {
        return dummyBytesAry;
    }

    /**
     * Updates the CRC value.
     */
    void updateCRC() {
        setCRC(0);
        CRC32 crcAlg = new CRC32();
        crcAlg.update(toByteArray());

        setCRC((int) crcAlg.getValue());
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

    
    protected void fromInputStream(final InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        packetVersion = din.readShort();
        packetTypeCode = din.readShort();
        crcValue = din.readInt();
        resultCode = din.readShort();
        din.readFully(byteBufferAry);
        din.readFully(dummyBytesAry);
    }
}
