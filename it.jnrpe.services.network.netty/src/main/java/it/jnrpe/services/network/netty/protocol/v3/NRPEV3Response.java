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
package it.jnrpe.services.network.netty.protocol.v3;

import it.jnrpe.engine.services.commands.ExecutionResult;
import java.util.Arrays;

public class NRPEV3Response extends NRPEV3AbstractPacket {
  public NRPEV3Response(ExecutionResult result) {
    super(
        2,
        0,
        result.getStatus().ordinal(),
        0,
        messageToBuffer(result.getMessage()),
        new byte[] {0, 0, 0});
    updateCRC();
  }

  private static byte[] messageToBuffer(String msg) {
    byte[] msgBytes = msg.getBytes();
    return Arrays.copyOf(msg.getBytes(), msgBytes.length + 1);
  }
}
