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
package it.jnrpe.engine.provider.auth;

import it.jnrpe.engine.services.auth.IAction;
import it.jnrpe.engine.services.auth.IAuthService;
import it.jnrpe.engine.services.config.ConfigurationManager;
import it.jnrpe.engine.services.config.IJNRPEConfig;
import java.util.*;

public class SimpleAuthProvider implements IAuthService {
  private static final Map<String, Date> OTP = Collections.synchronizedMap(new HashMap<>());
  private static final long MAX_LIFESPAN = 2 * 60 * 1000;

  @FunctionalInterface
  static interface IAuthConfigurationProvider {
    public Optional<IJNRPEConfig> getConfiguration();
  }

  private final IAuthConfigurationProvider configurationProvider;

  public SimpleAuthProvider() {
    this.configurationProvider = ConfigurationManager::getConfig;
    Timer timer = new Timer("AUTH-OTP-EXPIRATION-TIMER");
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            final var now = System.currentTimeMillis();
            OTP.entrySet().removeIf(entry -> (entry.getValue().getTime() - now) > MAX_LIFESPAN);
          }
        },
        30 * 1000L);
  }

  SimpleAuthProvider(IAuthConfigurationProvider configurationProvider) {
    this.configurationProvider = configurationProvider;
  }

  @Override
  public String getName() {
    return "SIMPLEAUTH";
  }

  @Override
  public Optional<String> getAuthToken(Map<String, ?> credentials) {
    if (credentials == null) {
      return Optional.empty();
    }

    // Required keys:
    // BINDING: BindingIP:PORT
    // SRC_IP: ip of the caller
    if (!credentials.containsKey("BINDING") || !(credentials.get("BINDING") instanceof String)) {
      // TODO: log this event
      return Optional.empty();
    }
    String binding = (String) credentials.get("BINDING");
    String srcIp = (String) credentials.get("SRC_IP");

    if (configurationProvider
        .getConfiguration()
        .flatMap(
            c ->
                c.getServer().bindings().stream()
                    .filter(b -> (b.ip() + ":" + b.port()).equals(binding))
                    .findFirst()
                    .map(b -> b.allow().isEmpty() || b.allow().contains(srcIp))
                    .or(() -> Optional.of(false)))
        .or(() -> Optional.of(false))
        .get()) {
      String uuid = UUID.randomUUID().toString();
      OTP.put(uuid, new Date());
      return Optional.of(uuid);
    }

    return Optional.empty();
  }

  @Override
  public Optional<String> getAuthToken() {
    return Optional.empty();
  }

  @Override
  public boolean authorize(String token) {
    return OTP.remove(token) != null;
  }

  @Override
  public boolean authorize(String token, IAction action) {
    return authorize(token);
  }
}
