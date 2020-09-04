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
package it.jnrpe.network.decoders;

import it.jnrpe.network.protocol.ProtocolPacket;
import it.jnrpe.network.protocol.v2.NRPEV2Request;

class DecoderV2Builder {
  private static class RequestV2Builder extends AbstractPacketBuilder {
    protected void loadRequestFromBuffer() {
      byte[] reqBuffer = new byte[1024];
      this.buffer.readBytes(reqBuffer);
      this.setRequestBuffer(reqBuffer);

      byte[] padding = new byte[2];
      this.buffer.readBytes(padding);
      this.setPadding(padding);
    }

    @Override
    protected ProtocolPacket buildPacket() {
      return new NRPEV2Request(getCrc32(), getResultCode(), getRequestBuffer(), getPadding());
    }
  }

  public static IPacketBuilder forPacket(int type) {
    switch (type) {
      case 1:
        return new RequestV2Builder();
      default: // FIXME: throw an exception
        return null;
    }
  }
}
