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
package it.jnrpe.engine.services.auth;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The interface for the authentication service.
 *
 * <p>This interface provides methods for getting an authentication and authorizing a token.
 */
public interface IAuthService {
  /**
   * Gets a list of all authentication providers.
   *
   * @return A list of all authentication providers.
   */
  static List<IAuthService> getProviders() {
    return ServiceLoader.load(IAuthService.class).stream()
        .map(ServiceLoader.Provider::get)
        .collect(Collectors.toList());
  }

  /**
   * Gets the name of the authentication service.
   *
   * @return The name of the authentication service.
   */
  String getName();

  /**
   * Gets an authentication token for the given credentials.
   *
   * @param credentials The credentials to use for authentication. Its content depends on the
   *     authentication provider.
   * @return An optional authentication token, if the credentials are valid.
   */
  Optional<String> getAuthToken(Map<String, ?> credentials);

  /**
   * Gets an authentication token for the current user.
   *
   * @return An optional authentication token, if the current user is authenticated.
   */
  Optional<String> getAuthToken();

  /**
   * Authorizes the given token.
   *
   * @param token The token to authorize.
   * @return True if the token is valid, false otherwise.
   */
  boolean authorize(String token);

  /**
   * Authorizes the given token for the given action.
   *
   * @param token The token to authorize.
   * @param action The action to authorize.
   * @return True if the token is valid and the action is authorized, false otherwise.
   */
  boolean authorize(String token, IAction action);
}
