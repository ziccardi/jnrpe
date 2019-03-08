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

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Data can be read from InputStream or from a ByteBuf. This class returns a common interface to access both inputs.
 */
public abstract class DataStreamAdapter {

    private static class InputStreamDataStreamAdapter extends DataStreamAdapter {
        private final DataInputStream din;

        private InputStreamDataStreamAdapter(InputStream in) {
            this.din = new DataInputStream(in);
        }

        public int readUnisgnedInt16() throws IOException {
            return din.readShort();
        }

        public int readUnisgnedInt32() throws IOException {
            return din.readInt();
        }

        public int readFully(byte[] out) throws IOException {
            return din.read(out);
        }
    }

    private static class ByteBufDataStreamAdapter extends DataStreamAdapter {
        private final ByteBuf byteBuf;

        private ByteBufDataStreamAdapter(final ByteBuf byteBuf) {
            this.byteBuf = byteBuf;
        }

        public int readUnisgnedInt16() {
            return byteBuf.readUnsignedShort();
        }

        public int readUnisgnedInt32() {
            return byteBuf.readInt();
        }

        public int readFully(byte[] out) {
            byteBuf.readBytes(out);
            return out.length;
        }

    }


    public abstract int readUnisgnedInt16() throws IOException;

    public abstract int readUnisgnedInt32() throws IOException;

    public abstract int readFully(byte[] out) throws IOException;

    public static DataStreamAdapter forData(InputStream in) {
        return new InputStreamDataStreamAdapter(in);
    }

    public static DataStreamAdapter forData(ByteBuf in) {
        return new ByteBufDataStreamAdapter(in);
    }
}
