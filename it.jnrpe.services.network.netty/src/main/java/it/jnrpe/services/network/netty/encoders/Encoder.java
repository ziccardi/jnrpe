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

import it.jnrpe.engine.services.commands.ExecutionResult;
import java.net.ProtocolException;
import java.util.function.Function;

public class Encoder {
  private final Function<ExecutionResult, IResponseEncoder> encoderSupplier;

  private Encoder(Function<ExecutionResult, IResponseEncoder> encoderSupplier) {
    this.encoderSupplier = encoderSupplier;
  }

  public static Encoder forVersion(int version) throws ProtocolException {
    switch (version) {
      case 2:
        return new Encoder(V2Encoder::new);
      case 3:
        return new Encoder(V3Encoder::new);
      case 4:
        return new Encoder(V4Encoder::new);
      default:
        throw new ProtocolException("Invalid packet version received (" + version + ")");
    }
  }

  public IResponseEncoder andResult(final ExecutionResult res) {
    return this.encoderSupplier.apply(res);
  }
}
