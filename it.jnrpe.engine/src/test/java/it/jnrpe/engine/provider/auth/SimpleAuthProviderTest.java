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
package it.jnrpe.engine.provider.auth;

import static org.junit.jupiter.api.Assertions.*;

import it.jnrpe.engine.services.auth.IAction;
import it.jnrpe.engine.services.config.JNRPEConfig;
import java.util.*;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class SimpleAuthProviderTest {

  private SimpleAuthProvider getAuthProvider() {
    return getAuthProvider(() -> new JNRPEConfig().withBinding("127.0.0.1", 8080, false));
  }

  private SimpleAuthProvider getAuthProvider(Supplier<JNRPEConfig> configSupplier) {
    final JNRPEConfig config = configSupplier != null ? configSupplier.get() : new JNRPEConfig();

    return new SimpleAuthProvider(() -> Optional.of(config));
  }

  @Test
  void testGetName() {
    assertEquals("SIMPLEAUTH", getAuthProvider().getName());
  }

  @Test
  void testGetAuthTokenWithValidCredentials() {
    // Prepare valid credentials
    Map<String, Object> credentials = new HashMap<>();
    credentials.put("BINDING", "127.0.0.1:8080");
    credentials.put("SRC_IP", "192.168.0.1");
    Optional<String> authToken = getAuthProvider().getAuthToken(credentials);
    assertTrue(authToken.isPresent());
  }

  @Test
  void testGetAuthTokenWithInvalidCredentials() {
    // Prepare invalid credentials
    Map<String, Object> credentials = new HashMap<>();
    credentials.put("BINDING", "127.0.0.1:8080");
    credentials.put("SRC_IP", "192.168.0.2");

    // With SimpleAuthToken an invalid credential is a not allowed IP address
    Optional<String> authToken =
        getAuthProvider(
                () ->
                    new JNRPEConfig()
                        .withBinding("127.0.0.1", 8080, false)
                        .withAllowedAddress("127.0.0.1", 8080, "127.0.0.1"))
            .getAuthToken(credentials);
    assertFalse(authToken.isPresent());
  }

  @Test
  void testGetAuthTokenWithoutCredentials() {
    Optional<String> authToken = getAuthProvider().getAuthToken(Collections.emptyMap());
    assertFalse(authToken.isPresent());
  }

  @Test
  void testGetAuthTokenWithoutBinding() {
    Map<String, Object> credentials = new HashMap<>();
    credentials.put("SRC_IP", "192.168.0.1");
    Optional<String> authToken = getAuthProvider().getAuthToken(credentials);
    assertFalse(authToken.isPresent());
  }

  @Test
  void testGetAuthTokenWithoutSrcIp() {
    Map<String, Object> credentials = new HashMap<>();
    credentials.put("BINDING", "127.0.0.1:8080");
    Optional<String> authToken =
        getAuthProvider(() -> new JNRPEConfig().withBinding("127.0.0.1", 8080, false))
            .getAuthToken(credentials);
    assertTrue(authToken.isPresent());

    authToken =
        getAuthProvider(
                () ->
                    new JNRPEConfig()
                        .withBinding("127.0.0.1", 8080, false)
                        .withAllowedAddress("127.0.0.1", 8080, "127.0.0.1"))
            .getAuthToken(credentials);

    assertFalse(authToken.isPresent());
  }

  @Test
  void testGetAuthTokenWithNullCredentials() {
    Optional<String> authToken = getAuthProvider().getAuthToken(null);
    assertFalse(authToken.isPresent());
  }

  @Test
  void testGetAuthTokenWithValidToken() {
    Map<String, Object> credentials = new HashMap<>();
    credentials.put("BINDING", "127.0.0.1:8080");
    credentials.put("SRC_IP", "192.168.0.2");

    getAuthProvider()
        .getAuthToken(credentials)
        .ifPresentOrElse(
            token -> {
              assertTrue(getAuthProvider().authorize(token));
            },
            () -> fail("Token not returned"));
  }

  @Test
  void testGetAuthTokenWithInvalidToken() {
    String invalidToken = "invalid-token";
    assertFalse(getAuthProvider().authorize(invalidToken));
  }

  @Test
  void testAuthorizeWithAction() {
    Map<String, Object> credentials = new HashMap<>();
    credentials.put("BINDING", "127.0.0.1:8080");
    credentials.put("SRC_IP", "192.168.0.2");

    getAuthProvider()
        .getAuthToken(credentials)
        .ifPresentOrElse(
            token -> {
              IAction action = new SomeAction();
              assertTrue(getAuthProvider().authorize(token, action));
            },
            () -> fail("Token not returned"));
  }

  private static class SomeAction implements IAction {
    @Override
    public String getActionId() {
      return "TEST ACTION";
    }
  }
}
