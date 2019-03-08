/*******************************************************************************
 * Copyright (c) 2007, 2019 Massimiliano Ziccardi
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
package it.jnrpe.net.packet;

import it.jnrpe.net.PacketType;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public abstract class AbstractJNRPEProtocolPacket implements IJNRPEProtocolPacket {
    private final int version;
    private int type;
    private int crc32;
    private int resultCode;

    protected AbstractJNRPEProtocolPacket(int version) {
        this.version = version;
    }

    protected abstract void finishLoading(final DataStreamAdapter in) throws IOException;
    protected abstract void finishWriting(final DataOutputStream dout) throws IOException;
    protected abstract byte[] getBuffer();

    protected final IJNRPEProtocolPacket load(final DataStreamAdapter din) throws IOException {
        this.type = din.readUnisgnedInt16();
        this.crc32 = din.readUnisgnedInt32();
        this.resultCode = din.readUnisgnedInt16();
        this.finishLoading(din);
        return this;
    }

    public void setType(final PacketType packetType) {
        this.type = packetType.intValue();
    }

    public int getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }

    public int getResultCode() {
        return resultCode;
    }

    @Override
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(version);
            dout.writeShort(type);
            dout.writeInt(crc32);
            dout.writeShort(resultCode);
            finishWriting(dout);
            dout.close();
        } catch (IOException e) {
            // Never happens...
            throw new IllegalStateException(e.getMessage(), e);
        }
        return bout.toByteArray();
    }


    /**
     * Updates the CRC value.
     */
    public void updateCRC() {
        this.crc32 = 0;
        CRC32 crcAlg = new CRC32();
        crcAlg.update(this.toByteArray());

        this.crc32 = (int) crcAlg.getValue();
    }

    public boolean isValid() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(this.version);
            dout.writeShort(this.type);
            dout.writeInt(0); // NO CRC
            dout.writeShort(resultCode);
            finishWriting(dout);
            dout.close();

            byte[] vBytes = bout.toByteArray();
            CRC32 crcAlg = new CRC32();
            crcAlg.update(vBytes);

            if (!(((int) crcAlg.getValue()) == this.crc32)) {
                return false;
            }
        } catch (IOException e) {
            // Never happens...
            throw new IllegalStateException(e.getMessage(), e);
        }
        return true;
    }

    /**
     * @return The string representation of the buffer.
     */
    public String getBufferAsString() {
        byte[] buffer = getBuffer();
        int zeroIndex = ArrayUtils.indexOf(buffer, (byte) 0);

        if (zeroIndex == ArrayUtils.INDEX_NOT_FOUND) {
            return new String(buffer);
        } else {
            return new String(buffer, 0, zeroIndex);
        }
    }
}
