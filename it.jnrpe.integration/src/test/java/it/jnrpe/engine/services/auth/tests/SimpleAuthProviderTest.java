/*******************************************************************************
 * Copyright (C) 2022, Massimiliano Ziccardi
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
package it.jnrpe.engine.services.auth.tests;

import it.jnrpe.engine.services.auth.IAuthService;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleAuthProviderTest {
  private final IAuthService authService =
      IAuthService.getInstances().stream().findFirst().orElseThrow();

  @Test
  public void testValidRequest() {
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5667", "SRC_IP", "127.0.0.2"))
        .orElseThrow();
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5667", "SRC_IP", "127.0.0.3"))
        .orElseThrow();
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5667", "SRC_IP", "127.0.0.4"))
        .orElseThrow();
  }

  @Test
  public void testInvalidRequest() {
    Assertions.assertFalse(
        authService
            .getAuthToken(Map.of("BINDING", "127.0.0.1:5667", "SRC_IP", "127.0.0.5"))
            .isPresent(),
        "Request should have been blocked");
  }

  @Test
  public void testAuthorizeRequest() {
    var token =
        authService
            .getAuthToken(Map.of("BINDING", "127.0.0.1:5667", "SRC_IP", "127.0.0.2"))
            .orElseThrow();
    Assertions.assertTrue(authService.authorize(token), "Request should have been authorised");
  }

  @Test
  public void testReusingToken() {
    var token =
        authService
            .getAuthToken(Map.of("BINDING", "127.0.0.1:5667", "SRC_IP", "127.0.0.2"))
            .orElseThrow();
    Assertions.assertTrue(authService.authorize(token), "Request should have been authorised");
    Assertions.assertFalse(authService.authorize(token), "Request should have been blocked");
  }

  @Test
  public void testAllowAll() {
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5668", "SRC_IP", "127.0.0.2"))
        .orElseThrow();
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5668", "SRC_IP", "127.0.0.3"))
        .orElseThrow();
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5668", "SRC_IP", "127.0.0.4"))
        .orElseThrow();
    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5668", "SRC_IP", "127.0.0.9"))
        .orElseThrow();

    authService
        .getAuthToken(Map.of("BINDING", "127.0.0.1:5668", "SRC_IP", "10.10.10.10"))
        .orElseThrow();
  }
}
