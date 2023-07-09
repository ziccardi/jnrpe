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
package it.jnrpe.engine.events;

import static org.mockito.Mockito.*;

import it.jnrpe.engine.services.events.IEventManager;
import it.jnrpe.engine.services.events.LogEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventManagerTest {
  it.jnrpe.engine.services.events.IEventManager evtMgr1;
  it.jnrpe.engine.services.events.IEventManager evtMgr2;

  @BeforeEach
  public void setup() throws Exception {
    evtMgr1 = mock(it.jnrpe.engine.services.events.IEventManager.class);
    evtMgr2 = mock(it.jnrpe.engine.services.events.IEventManager.class);
    setupTestEventManagers();
  }

  @Test
  public void testEmitWithMessage() {
    // Positive test case
    EventManager.emit(LogEvent.INFO, "Test message");

    verify(evtMgr1, times(1)).onEvent(LogEvent.INFO, "Test message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.INFO, "Test message");
  }

  @Test
  public void testEmitWithMessageAndException() {
    Throwable exception = new RuntimeException("Test exception");
    // Positive test case
    EventManager.emit(LogEvent.ERROR, "Test message", exception);
    verify(evtMgr1, times(1)).onEvent(LogEvent.ERROR, "Test message", exception);
    verify(evtMgr2, times(1)).onEvent(LogEvent.ERROR, "Test message", exception);
  }

  @Test
  public void testTrace() {
    // Positive test case
    EventManager.trace("Test trace message");
    verify(evtMgr1, times(1)).onEvent(LogEvent.TRACE, "Test trace message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.TRACE, "Test trace message");
  }

  @Test
  public void testDebug() {
    // Positive test case
    EventManager.debug("Test debug message");
    verify(evtMgr1, times(1)).onEvent(LogEvent.DEBUG, "Test debug message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.DEBUG, "Test debug message");
  }

  @Test
  public void testInfo() {
    // Positive test case
    EventManager.info("Test info message");
    verify(evtMgr1, times(1)).onEvent(LogEvent.INFO, "Test info message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.INFO, "Test info message");
  }

  @Test
  public void testWarn() {
    // Positive test case
    EventManager.warn("Test warn message");
    verify(evtMgr1, times(1)).onEvent(LogEvent.WARN, "Test warn message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.WARN, "Test warn message");
  }

  @Test
  public void testErrorWithMessage() {
    // Positive test case
    EventManager.error("Test error message");
    verify(evtMgr1, times(1)).onEvent(LogEvent.ERROR, "Test error message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.ERROR, "Test error message");
  }

  @Test
  public void testErrorWithException() {
    Throwable exception = new RuntimeException("Test exception");
    // Positive test case
    EventManager.error(EventManager.withException(exception));
    verify(evtMgr1, times(1)).onEvent(LogEvent.ERROR, exception.getMessage(), exception);
    verify(evtMgr2, times(1)).onEvent(LogEvent.ERROR, exception.getMessage(), exception);
  }

  @Test
  public void testErrorWithExceptionAndParameterizedMessage() {
    Throwable exception = new RuntimeException("Test exception");
    // Positive test case
    EventManager.error(
        EventManager.withException(exception),
        EventManager.withMessage("This is a %s message. Number %d", "test", 51));
    verify(evtMgr1, times(1))
        .onEvent(LogEvent.ERROR, "This is a test message. Number 51", exception);
    verify(evtMgr2, times(1))
        .onEvent(LogEvent.ERROR, "This is a test message. Number 51", exception);
  }

  @Test
  public void testFatal() {
    // Positive test case
    EventManager.fatal("Test fatal message");
    verify(evtMgr1, times(1)).onEvent(LogEvent.FATAL, "Test fatal message");
    verify(evtMgr2, times(1)).onEvent(LogEvent.FATAL, "Test fatal message");
  }

  private void setupTestEventManagers() throws Exception {
    // Access the private plugin list field...
    Field evtManagersField = EventManager.class.getDeclaredField("eventManagers");
    evtManagersField.setAccessible(true);

    var eventManagerCollection =
        (Collection<IEventManager>) evtManagersField.get(null); // new ArrayList<IEventManager>();

    // Remove existing values
    eventManagerCollection.removeAll(Arrays.stream(eventManagerCollection.toArray()).toList());

    // Add the plugins to the repository
    eventManagerCollection.add(evtMgr1);
    eventManagerCollection.add(evtMgr2);
  }
}
