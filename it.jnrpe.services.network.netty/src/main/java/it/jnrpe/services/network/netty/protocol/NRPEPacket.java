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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;

public class NRPEPacket {
  private int version;
  private int packetType; // must be 1 for requests
  private long crc32;
  private int resultCode;
  private int alignment;

  private long bufferLength = 0;
  private byte[] buffer;
  private byte[] padding;

  public NRPEPacket() {}

  public NRPEPacket(
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
    this.buffer = Arrays.copyOf(buffer, buffer.length);
    this.bufferLength = buffer.length;
    this.padding = Arrays.copyOf(padding, padding.length);
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
    return Arrays.copyOf(this.padding, this.padding.length);
  }

  public int getAlignment() {
    return alignment;
  }

  public byte[] getBuffer() {
    return Arrays.copyOf(buffer, buffer.length);
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

  protected final long crc32() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);

    try {
      dout.writeShort(this.getVersion());
      dout.writeShort(this.getPacketType());
      dout.writeInt(0); // NO CRC
      dout.writeShort(this.getResultCode());
      if (this.getVersion() > 2) {
        dout.writeShort(this.getAlignment());
        dout.writeInt(this.getBuffer().length);
      }
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

  public final boolean validateCRC() {
    return this.crc32 == crc32();
  }

  public void updateCRC() {
    this.crc32 = this.crc32();
  }

  public void setCrc32(long crc32) {
    this.crc32 = crc32;
  }

  public long getBufferLength() {
    return bufferLength;
  }

  public void setBufferLength(long bufferLength) {
    this.bufferLength = bufferLength;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public void setPacketType(int packetType) {
    this.packetType = packetType;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  public void setBuffer(byte[] buffer) {
    this.buffer = Arrays.copyOf(buffer, buffer.length);
  }

  public void setPadding(byte[] padding) {
    this.padding = Arrays.copyOf(padding, padding.length);
  }
}
