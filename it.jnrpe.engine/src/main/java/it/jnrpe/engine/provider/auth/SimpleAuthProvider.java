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
import java.util.*;

public class SimpleAuthProvider implements IAuthService {
  private static final Map<String, Date> OTP = Collections.synchronizedMap(new HashMap<>());
  private static final long MAX_LIFESPAN = 2 * 60 * 1000;

  public SimpleAuthProvider() {
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

  @Override
  public String getName() {
    return "SIMPLEAUTH";
  }

  @Override
  public Optional<String> getAuthToken(Map<String, ?> credentials) {
    // Required keys:
    // BINDING: BindingIP:PORT
    // SRC_IP: ip of the caller
    assert credentials.containsKey("BINDING");
    assert credentials.get("BINDING") instanceof String;
    String binding = (String) credentials.get("BINDING");
    String srcIp = (String) credentials.get("SRC_IP");

    if (ConfigurationManager.getConfig()
        .flatMap(
            c ->
                c.getServer().getBindings().stream()
                    .filter(b -> (b.getIp() + ":" + b.getPort()).equals(binding))
                    .findFirst()
                    .map(b -> b.getAllow().isEmpty() || b.getAllow().contains(srcIp))
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
