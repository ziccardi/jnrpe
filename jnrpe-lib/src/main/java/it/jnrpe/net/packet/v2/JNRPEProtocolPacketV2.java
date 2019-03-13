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
package it.jnrpe.net.packet.v2;

import it.jnrpe.net.packet.AbstractJNRPEProtocolPacket;
import it.jnrpe.net.packet.DataStreamAdapter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class JNRPEProtocolPacketV2 extends AbstractJNRPEProtocolPacket {

    public JNRPEProtocolPacketV2() {
        super(2);
    }

    /**
     * Max amount of data we'll send in one query/response.
     */
    private static final int MAX_PACKETBUFFER_LENGTH = 1024;

    /**
     * The packet buffer.
     */
    private byte[] byteBufferAry = new byte[MAX_PACKETBUFFER_LENGTH];
    private byte[] dummy = new byte[2];

    protected byte[] getBuffer() {
        return byteBufferAry;
    }

    /**
     * Initializes the arrays with random data. Not sure it is really needed...
     */
    private void initRandomBuffer() {
        Random rnd = new Random(System.currentTimeMillis());
        rnd.nextBytes(byteBufferAry);
    }

    /**
     * Sets the value of the data buffer.
     *
     * @param buffer the buffer value
     */
    public void setBuffer(final String buffer) {
        initRandomBuffer();
        byteBufferAry = Arrays.copyOf(buffer.getBytes(), MAX_PACKETBUFFER_LENGTH);
    }

    @Override
    protected void finishLoading(DataStreamAdapter in) throws IOException {
        in.readFully(byteBufferAry);
        in.readFully(dummy);
    }

    @Override
    protected void finishWriting(DataOutputStream dout) throws IOException {
        dout.write(this.byteBufferAry);
        dout.write(this.dummy);
    }
}
