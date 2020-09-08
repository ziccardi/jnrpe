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
package it.jnrpe.services.network.netty.decoders;

import it.jnrpe.services.network.netty.protocol.ProtocolPacket;
import it.jnrpe.services.network.netty.protocol.v3.NRPEV3Request;

class DecoderV3Builder {
  protected static class RequestV3Builder extends AbstractPacketBuilder {
    public void loadRequestFromBuffer() {
      setAlignment(this.buffer.readUnsignedShort());

      long bufLength = this.buffer.readUnsignedInt();
      if (bufLength < 0 || bufLength > 65536) {
        // FIXME: throw an exception
      }
      byte[] requestBuffer = new byte[(int) bufLength];
      this.buffer.readBytes(requestBuffer);
      setRequestBuffer(requestBuffer);

      byte[] padding = new byte[(int) (1020 - bufLength)];
      this.buffer.readBytes(padding);
      setPadding(padding);
    }

    @Override
    protected ProtocolPacket buildPacket() {
      return new NRPEV3Request(getCrc32(), getAlignment(), getRequestBuffer(), getPadding());
    }
  }

  public static IPacketBuilder forPacket(int type) {
    switch (type) {
      case 1:
        return new RequestV3Builder();
      default: // FIXME: throw an exception
        return null;
    }
  }
}
