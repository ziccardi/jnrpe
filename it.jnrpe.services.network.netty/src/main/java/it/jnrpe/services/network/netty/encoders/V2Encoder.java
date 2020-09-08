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
package it.jnrpe.services.network.netty.encoders;

import it.jnrpe.engine.services.network.ExecutionResult;
import it.jnrpe.services.network.netty.protocol.ProtocolPacket;
import it.jnrpe.services.network.netty.protocol.v2.NRPEV2Response;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

class V2Encoder implements IResponseEncoder {
  private final ProtocolPacket response;

  public V2Encoder(final ExecutionResult result) {
    this.response = new NRPEV2Response(result);
  }

  @Override
  public byte[] encode() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(bout);

    try {
      dout.writeShort(response.getVersion()); // Version 2
      dout.writeShort(response.getPacketType()); // Type: Response
      dout.writeInt((int) response.getCrc32()); // Type: Response
      dout.writeShort(response.getResultCode());
      dout.write(response.getBuffer());
      dout.write(response.getPadding());
      dout.flush();
    } catch (Exception e) {

    }

    return bout.toByteArray();
  }
}
