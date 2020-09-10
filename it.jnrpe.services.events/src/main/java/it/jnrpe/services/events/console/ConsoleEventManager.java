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
package it.jnrpe.services.events.console;

import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.engine.services.events.IEventType;
import java.util.Date;

public class ConsoleEventManager implements IEventManager {
  @Override
  public void onEvent(IEventType type, String message, Throwable exc) {
    onEvent(type, message);
    exc.printStackTrace();
  }

  @Override
  public void onEvent(IEventType type, String message) {
    final Date timestamp = new Date();
    System.out.println(
        String.format("%tF %tT [%s] - %s", timestamp, timestamp, type.type(), message));
  }
}
