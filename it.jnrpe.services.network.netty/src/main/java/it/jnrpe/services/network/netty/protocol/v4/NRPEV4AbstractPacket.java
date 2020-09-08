/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.services.network.netty.protocol.v4;

import it.jnrpe.services.network.netty.protocol.ProtocolPacket;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

abstract class NRPEV4AbstractPacket extends ProtocolPacket {
  public NRPEV4AbstractPacket(
      int type, long crc32, int resultCode, int alignment, byte[] buffer, byte[] padding) {
    super(4, type, crc32, resultCode, alignment, buffer, padding);
  }

  protected long crc32() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);

    try {
      dout.writeShort(this.getVersion());
      dout.writeShort(this.getPacketType());
      dout.writeInt(0); // NO CRC
      dout.writeShort(this.getResultCode());
      dout.writeShort(this.getAlignment());
      dout.writeInt(this.getBuffer().length);
      dout.write(this.getBuffer());
      dout.write(this.getPadding());
      dout.close();

      byte[] bytes = bout.toByteArray();
      CRC32 crcAlg = new CRC32();
      crcAlg.update(bytes);

      return crcAlg.getValue();
    } catch (IOException e) {
      // Never happens...
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
