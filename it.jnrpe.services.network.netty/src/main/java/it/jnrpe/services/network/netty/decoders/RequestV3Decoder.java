/*******************************************************************************
 * Copyright (C) 2022, Massimiliano Ziccardi
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

import io.netty.buffer.ByteBuf;
import it.jnrpe.services.network.netty.protocol.NRPEPacket;
import it.jnrpe.services.network.netty.protocol.v3.NRPEV3Request;

class RequestV3Decoder extends AbstractPacketDecoder {
  public void loadRequestFromBuffer(final ByteBuf buffer) {
    setAlignment(buffer.readUnsignedShort());

    long bufLength = buffer.readUnsignedInt();
    if (bufLength < 0 || bufLength > 65536) {
      // FIXME: throw an exception
    }
    byte[] requestBuffer = new byte[(int) bufLength];
    buffer.readBytes(requestBuffer);
    setRequestBuffer(requestBuffer);

    byte[] padding = new byte[(int) (1020 - bufLength)];
    buffer.readBytes(padding);
    setPadding(padding);
  }

  @Override
  protected NRPEPacket buildPacket() {
    return new NRPEV3Request(getCrc32(), getAlignment(), getRequestBuffer(), getPadding());
  }
}
