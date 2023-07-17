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

/**
 * The {@link EventManager} class is a utility class that provides methods for emitting events and
 * logging messages. It also allows for customization of events through the use of event options.
 *
 * <p>This class provides a central way to log system events.
 */
public class EventManager {
  private static final Collection<IEventManager> eventManagers = IEventManager.getProviders();

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

  /**
   * Returns an event option that sets the event's message. The message is formatted using the
   * specified format and parameters. <br>
   * The following code sets the event's message to "Error: Something went wrong" using the
   * withMessage() option:
   *
   * <pre>
   * error(withMessage("Error: Something went wrong"));
   * </pre>
   *
   * @param format The message format.
   * @param args The message parameters.
   * @return The event option. Parameters:
   * @see #withException(Throwable)
   */
  public static IEventOption withMessage(final String format, final Object... args) {
    return (event) -> event.setMessage(String.format(format, (Object[]) args));
  }

  /**
   * Returns an event option that sets the event's exception.
   *
   * <p>This option can be used to associate an exception with an event.
   *
   * @param exc The exception.
   * @return The event option.
   * @see #withMessage(String, Object...)
   *     <p>For example, the following code sets the event's exception to a `RuntimeException`:
   *     <pre>
   * error(withException(new RuntimeException()));
   * </pre>
   */
  public static IEventOption withException(final Throwable exc) {
    return (event) -> event.setException(exc);
  }

  /**
   * Emits an event.
   *
   * <p>This method emits an event of the specified type with the specified message. The event will
   * be dispatched to all the registered event managers.
   *
   * @param type The event type.
   * @param message The event message.
   * @see IEventType
   * @see IEventManager
   *     <p>For example, the following code emits an `ERROR` event with the message "Something went
   *     wrong":
   *     <pre>
   * emit(IEventType.ERROR, "Something went wrong");
   * </pre>
   */
  public static void emit(IEventType type, String message) {
    eventManagers.forEach(eventManager -> eventManager.onEvent(type, message));
  }

  /**
   * Emits an event.
   *
   * <p>This method emits an event of the specified type with the specified message and exception.
   * The event will be dispatched to all the registered event managers.
   *
   * @param type The event type.
   * @param message The event message.
   * @param exc The event exception.
   * @see IEventType
   * @see IEventManager
   *     <p>For example, the following code emits an `ERROR` event with the message "Something went
   *     wrong" and the exception `RuntimeException`:
   *     <pre>
   * emit(IEventType.ERROR, "Something went wrong", new RuntimeException());
   * </pre>
   */
  public static void emit(IEventType type, String message, Throwable exc) {
    eventManagers.forEach(eventManager -> eventManager.onEvent(type, message, exc));
  }

  /**
   * Logs a message at the TRACE level.
   *
   * @param format The message to log.
   * @param args The message parameters. See {@link String#format(String, Object...)} for syntax
   *     details.
   */
  public static void trace(String format, Object... args) {
    emitLog(LogEvent.TRACE, format, args);
  }

  /**
   * Logs a message at the DEBUG level.
   *
   * @param format The message to log.
   * @param args The message parameters. See {@link String#format(String, Object...)} for syntax
   *     details.
   */
  public static void debug(String format, Object... args) {
    emitLog(LogEvent.DEBUG, format, args);
  }

  /**
   * Logs a message at the INFO level.
   *
   * @param format The message to log.
   * @param args The message parameters. See {@link String#format(String, Object...)} for syntax
   *     details.
   */
  public static void info(String format, Object... args) {
    emitLog(LogEvent.INFO, format, args);
  }

  /**
   * Logs a message at the WARNING level.
   *
   * @param format The message to log.
   * @param args The message parameters. See {@link String#format(String, Object...)} for syntax
   *     details.
   */
  public static void warn(String format, Object... args) {
    emitLog(LogEvent.WARN, format, args);
  }

  /**
   * Logs a message at the ERROR level.
   *
   * @param format The message to log.
   * @param args The message parameters. See {@link String#format(String, Object...)} for syntax
   *     details.
   */
  public static void error(String format, Object... args) {
    emitLog(LogEvent.ERROR, format, args);
  }

  /**
   * Logs an error message.
   *
   * @param options The event options.
   * @see #emit(IEventType, String, Throwable)
   * @since 3.0.0
   *     <p>The only available options are:
   *     <ul>
   *       <li>{@link #withMessage(String, Object...)}
   *       <li>{@link #withException(Throwable)}
   *     </ul>
   *     For example, to set the message to "Error: Something went wrong" and the exception to a
   *     RuntimeException, you would use the following code:
   *     <pre>
   * error(
   *   withMessage("Error: Something went wrong"),
   *   withException(new RuntimeException())
   * );
   * </pre>
   *     The withMessage() option takes a message and a variable number of parameters, and formats
   *     the message using the parameters. The withException() option takes an exception and sets
   *     the event's exception to the exception.
   */
  public static void error(IEventOption... options) {
    emitLog(LogEvent.ERROR, options);
  }

  /**
   * Logs a message at the ERROR level.
   *
   * @param format The message to log.
   * @param args The message parameters. See {@link String#format(String, Object...)} for syntax
   *     details.
   */
  public static void fatal(String format, Object... args) {
    emitLog(LogEvent.FATAL, format, args);
  }

  /**
   * Logs a fatal message.
   *
   * @param options The event options.
   * @see #emit(IEventType, String, Throwable)
   * @since 3.0.0
   *     <p>The only available options are:
   *     <ul>
   *       <li>{@link #withMessage(String, Object...)}
   *       <li>{@link #withException(Throwable)}
   *     </ul>
   *     For example, to set the message to "Error: Something went wrong" and the exception to a
   *     RuntimeException, you would use the following code:
   *     <pre>
   * fatal(
   *   withMessage("Error: Something went wrong"),
   *   withException(new RuntimeException())
   * );
   * </pre>
   *     The withMessage() option takes a message and a variable number of parameters, and formats
   *     the message using the parameters. The withException() option takes an exception and sets
   *     the event's exception to the exception.
   */
  public static void fatal(IEventOption... options) {
    emitLog(LogEvent.FATAL, options);
  }

  private static void emitLog(IEventType type, String message, Object... msgparams) {
    emit(type, msgparams.length > 0 ? String.format(message, msgparams) : message);
  }

  private static void emitLog(IEventType severity, IEventOption... options) {
    Event evt = new Event();
    Arrays.stream(options).forEach(option -> option.apply(evt));

    if (evt.message == null && evt.exception == null) {
      fatal("Event manager `emitLog` method called with null message and null exception");
      return;
    }

    if (evt.message == null) {
      evt.message = evt.exception.getMessage();
    }

    emit(severity, evt.message, evt.exception);
  }
}
