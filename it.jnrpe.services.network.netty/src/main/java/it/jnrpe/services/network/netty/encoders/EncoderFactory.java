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

public class EncoderFactory {
  private EncoderFactory() {}

  public static IResponseEncoder produceEncoder(int version, ExecutionResult res) {
    switch (version) {
      case 2:
        return new V2Encoder(res);
      case 3:
        return new V3Encoder(res);
      case 4:
        return new V4Encoder(res);
      case 5:
        // FIXME: throw exception
        return null;
    }
    return new V2Encoder(res);
  }
}
