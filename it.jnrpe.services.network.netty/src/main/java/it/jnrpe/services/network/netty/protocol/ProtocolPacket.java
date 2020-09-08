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
package it.jnrpe.services.network.netty.protocol;

public abstract class ProtocolPacket {
  private final int version;
  private final int packetType; // must be 1 for requests
  private long crc32;
  private final int resultCode;
  private final int alignment;
  private final byte[] buffer;
  private final byte[] padding;

  public ProtocolPacket(
      int version,
      int type,
      long crc32,
      int resultCode,
      int alignment,
      byte[] buffer,
      byte[] padding) {
    this.version = version;
    this.packetType = type;
    this.crc32 = crc32;
    this.resultCode = resultCode;
    this.alignment = alignment;
    this.buffer = buffer;
    this.padding = padding;
  }

  public int getVersion() {
    return version;
  }

  public int getPacketType() {
    return packetType;
  }

  public long getCrc32() {
    return crc32;
  }

  public int getResultCode() {
    return resultCode;
  }

  public byte[] getPadding() {
    return this.padding;
  }

  public int getAlignment() {
    return alignment;
  }

  public byte[] getBuffer() {
    return buffer;
  }

  public String getCommand() {
    int i = 0;
    for (; i < buffer.length; i++) {
      if (buffer[i] == 0) break;
    }

    return new String(buffer, 0, i);
  }

  @Override
  public String toString() {
    return "ProtocolPacket{"
        + "version="
        + version
        + ", packetType="
        + packetType
        + ", crc32="
        + crc32
        + ", resultCode="
        + resultCode
        + ", alignment="
        + alignment
        + ", buffer="
        + getCommand()
        + '}';
  }

  protected abstract long crc32();

  public final boolean validateCRC() {
    return this.crc32 == crc32();
  }

  public void updateCRC() {
    this.crc32 = this.crc32();
  }
}
