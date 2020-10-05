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

public class LogEvent implements IEventType {
  private final int ordinal;
  private final String type;

  public static final LogEvent TRACE = new LogEvent(0, "TRACE");
  public static final LogEvent DEBUG = new LogEvent(1, "DEBUG");
  public static final LogEvent INFO = new LogEvent(2, "INFO");
  public static final LogEvent WARN = new LogEvent(3, "WARN");
  public static final LogEvent ERROR = new LogEvent(4, "ERROR");
  public static final LogEvent FATAL = new LogEvent(4, "FATAL");

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
