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
