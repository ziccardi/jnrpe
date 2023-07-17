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
package it.jnrpe.engine.services.events;

/**
 * The class for a log event.
 *
 * <p>This class implements the IEventType interface and provides constants for the different log
 * levels.
 */
public class LogEvent implements IEventType {

  /** The ordinal of the TRACE log level. */
  public static final int TRACE_ORDINAL = 0;

  /** The type of the TRACE log level. */
  public static final String TRACE_TYPE = "TRACE";

  /** The ordinal of the DEBUG log level. */
  public static final int DEBUG_ORDINAL = 1;

  /** The type of the DEBUG log level. */
  public static final String DEBUG_TYPE = "DEBUG";

  /** The ordinal of the INFO log level. */
  public static final int INFO_ORDINAL = 2;

  /** The type of the INFO log level. */
  public static final String INFO_TYPE = "INFO";

  /** The ordinal of the WARN log level. */
  public static final int WARN_ORDINAL = 3;

  /** The type of the WARN log level. */
  public static final String WARN_TYPE = "WARN";

  /** The ordinal of the ERROR log level. */
  public static final int ERROR_ORDINAL = 4;

  /** The type of the ERROR log level. */
  public static final String ERROR_TYPE = "ERROR";

  /** The ordinal of the FATAL log level. */
  public static final int FATAL_ORDINAL = 5;

  /** The type of the FATAL log level. */
  public static final String FATAL_TYPE = "FATAL";

  private final int ordinal;
  private final String type;

  public static final LogEvent TRACE = new LogEvent(TRACE_ORDINAL, TRACE_TYPE);
  public static final LogEvent DEBUG = new LogEvent(DEBUG_ORDINAL, DEBUG_TYPE);
  public static final LogEvent INFO = new LogEvent(INFO_ORDINAL, INFO_TYPE);
  public static final LogEvent WARN = new LogEvent(WARN_ORDINAL, WARN_TYPE);
  public static final LogEvent ERROR = new LogEvent(ERROR_ORDINAL, ERROR_TYPE);
  public static final LogEvent FATAL = new LogEvent(FATAL_ORDINAL, FATAL_TYPE);

  private LogEvent(int ordinal, String type) {
    this.ordinal = ordinal;
    this.type = type;
  }

  @Override
  public int ordinal() {
    return this.ordinal;
  }

  @Override
  public String type() {
    return this.type;
  }
}
