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
package it.jnrpe.engine.plugins.threshold;

public class ThresholdParsingException extends Exception {
  private int index;
  private String thresholdString;
  private String expectedToken;

  ThresholdParsingException() {
    super();
  }

  ThresholdParsingException(Throwable cause) {
    super(cause);
  }

  public int getIndex() {
    return index;
  }

  void setIndex(int index) {
    this.index = index;
  }

  public String getThresholdString() {
    return thresholdString;
  }

  void setThresholdString(String thresholdString) {
    this.thresholdString = thresholdString;
  }

  public String getExpectedToken() {
    return expectedToken;
  }

  void setExpectedToken(String expectedToken) {
    this.expectedToken = expectedToken;
  }
}
