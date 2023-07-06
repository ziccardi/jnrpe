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
import java.util.Arrays;
import java.util.List;

public class PacketDecoder implements IPacketDecoder {
  private final List<IPacketFieldReader> readers =
      Arrays.asList(
          new VersionReader(),
          new PacketTypeReader(),
          new CRC32Reader(),
          new ResultCodeReader(),
          new AlignmentReader(),
          new BufferLengthReader(),
          new BufferReader(),
          new PaddingReader());

  @Override
  public final NRPEPacket decode(final ByteBuf buffer) throws ProtocolException {
    NRPEPacket res = new NRPEPacket();
    readers.forEach((reader) -> reader.read(buffer, res));
    return res;
  }
}
