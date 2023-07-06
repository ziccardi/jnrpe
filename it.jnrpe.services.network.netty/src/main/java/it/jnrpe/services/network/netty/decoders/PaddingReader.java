/*******************************************************************************
 * Copyright (C) 2023, Massimiliano Ziccardi
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

public class PaddingReader implements IPacketFieldReader {
  public void read(final ByteBuf buffer, final NRPEPacket packet) {
    int paddingLength;
    if (packet.getVersion() >= 3) {
      paddingLength = 1020 - (int) packet.getBufferLength();
      if (paddingLength < 0) {
        paddingLength = 0;
      }
    } else {
      paddingLength = 2;
    }

    var padding = new byte[paddingLength];
    buffer.readBytes(padding);
    packet.setPadding(padding);
  }
}
