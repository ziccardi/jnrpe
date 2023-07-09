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
package it.jnrpe.engine.events;

import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.engine.services.events.IEventType;
import it.jnrpe.engine.services.events.LogEvent;
import java.util.Arrays;
import java.util.Collection;

public class EventManager {
  private static final Collection<IEventManager> eventManagers = IEventManager.getInstances();

  public static class Event {
    private String message;
    private Throwable exception;

    private void setMessage(String message) {
      this.message = message;
    }

    private void setException(Throwable exc) {
      this.exception = exc;
    }
  }

  @FunctionalInterface
  public interface IEventOption {
    void apply(Event evt);
  }

  public static IEventOption withMessage(final String message, final Object... messageParams) {
    return (event) -> event.setMessage(String.format(message, (Object[]) messageParams));
  }

  public static IEventOption withException(final Throwable exc) {
    return (event) -> event.setException(exc);
  }

  public static void emit(IEventType type, String message) {
    eventManagers.forEach(eventManager -> eventManager.onEvent(type, message));
  }

  public static void emit(IEventType type, String message, Throwable exc) {
    eventManagers.forEach(eventManager -> eventManager.onEvent(type, message, exc));
  }

  private static void emitLog(IEventType type, String message, Object... msgparams) {
    emit(type, msgparams.length > 0 ? String.format(message, msgparams) : message);
  }

  public static void trace(String message, Object... msgparams) {
    emitLog(LogEvent.TRACE, message, msgparams);
  }

  public static void debug(String message, Object... msgparams) {
    emitLog(LogEvent.DEBUG, message, msgparams);
  }

  public static void info(String message, Object... msgparams) {
    emitLog(LogEvent.INFO, message, msgparams);
  }

  public static void warn(String message, Object... msgparams) {
    emitLog(LogEvent.WARN, message, msgparams);
  }

  public static void error(String message, Object... msgparams) {
    emitLog(LogEvent.ERROR, message, msgparams);
  }

  public static void error(IEventOption... options) {
    Event evt = new Event();
    Arrays.stream(options).forEach(option -> option.apply(evt));

    if (evt.message == null && evt.exception == null) {
      fatal("Event manager called with null message and null exception");
      return;
    }

    if (evt.message == null) {
      evt.message = evt.exception.getMessage();
    }

    emit(LogEvent.ERROR, evt.message, evt.exception);
  }

  public static void fatal(String message, Object... msgparams) {
    emitLog(LogEvent.FATAL, message, msgparams);
  }
}
