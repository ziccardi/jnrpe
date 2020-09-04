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

import io.netty.buffer.ByteBuf;
import it.jnrpe.network.protocol.ProtocolPacket;

abstract class AbstractPacketBuilder implements IPacketBuilder {
  protected ByteBuf buffer;

  private long crc32;
  private int resultCode;
  private byte[] requestBuffer;
  private byte[] padding;
  private int alignment;

  public IPacketBuilder withByteBuf(final ByteBuf buffer) {
    this.buffer = buffer;
    return this;
  }

  @Override
  public final ProtocolPacket build() {
    int packetType = this.buffer.readUnsignedShort(); // must be 1 for requests
    if (packetType != 1) {
      // FIXME: throw exception
    }
    this.loadCommonData();
    this.loadRequestFromBuffer();
    return this.buildPacket();
  }

  private void loadCommonData() {
    this.crc32 = this.buffer.readUnsignedInt();
    this.resultCode = this.buffer.readUnsignedShort(); // empty for requests
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

  protected abstract void loadRequestFromBuffer();

  protected abstract ProtocolPacket buildPacket();
}
