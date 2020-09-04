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

public class DecoderBuilder {
  public static IPacketBuilder forPacket(int version, int type) {
    switch (version) {
      case 2:
        return DecoderV2Builder.forPacket(type);
      case 3:
        return DecoderV3Builder.forPacket(type);
      case 4:
        return DecoderV4Builder.forPacket(type);
      default:
        // fixme throw exception
        throw new IllegalStateException("Returning null - " + version + " - type " + type);
    }
  }
}
