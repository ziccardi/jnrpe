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

import io.netty.buffer.ByteBuf;
import it.jnrpe.services.network.netty.protocol.NRPEPacket;
import java.net.ProtocolException;

abstract class AbstractPacketDecoder implements IPacketDecoder {
  private long crc32;
  private int resultCode;
  private byte[] requestBuffer;
  private byte[] padding;
  private int alignment;

  @Override
  public final NRPEPacket decode(final ByteBuf buffer) throws ProtocolException {
    int packetType = buffer.readUnsignedShort(); // must be 1 for requests
    if (packetType != 1) {
      throw new ProtocolException(
          String.format(
              "Invalid packet type received. Expected '1' but received '%d'", packetType));
    }
    this.loadCommonData(buffer);
    this.loadRequestFromBuffer(buffer);
    return this.buildPacket();
  }

  private void loadCommonData(final ByteBuf buffer) {
    this.crc32 = buffer.readUnsignedInt();
    this.resultCode = buffer.readUnsignedShort(); // empty for requests
  }

  protected long getCrc32() {
    return crc32;
  }

  protected int getResultCode() {
    return resultCode;
  }

  protected byte[] getRequestBuffer() {
    return requestBuffer;
  }

  protected void setRequestBuffer(byte[] requestBuffer) {
    this.requestBuffer = requestBuffer;
  }

  protected byte[] getPadding() {
    return padding;
  }

  protected void setPadding(byte[] padding) {
    this.padding = padding;
  }

  protected int getAlignment() {
    return alignment;
  }

  protected void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  protected abstract void loadRequestFromBuffer(final ByteBuf buffer);

  protected abstract NRPEPacket buildPacket();
}
