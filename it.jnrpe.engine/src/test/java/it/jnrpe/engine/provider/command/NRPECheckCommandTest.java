/*******************************************************************************
 * Copyright (C) 2023, Massimiliano Ziccardi
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
package it.jnrpe.engine.provider.command;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.commands.ICommandInstance;
import it.jnrpe.engine.services.network.Status;
import org.junit.jupiter.api.Test;

public class NRPECheckCommandTest {
  @Test
  public void testExecute() {
    ICommandInstance commandInstance = new NRPECheckCommand().instantiate();
    ExecutionResult result = commandInstance.execute();
    assertEquals("JNRPE v3.0.0", result.getMessage());
    assertEquals(Status.OK, result.getStatus());
  }

  @Test
  public void testInstantiate() {
    NRPECheckCommand command = new NRPECheckCommand();
    ICommandInstance instance = command.instantiate();
    assertNotNull(instance);
  }
}
