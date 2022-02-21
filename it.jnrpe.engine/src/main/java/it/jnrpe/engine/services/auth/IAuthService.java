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

public interface IAuthService {
  static List<IAuthService> getInstances() {
    return ServiceLoader.load(IAuthService.class).stream()
        .map(ServiceLoader.Provider::get)
        .collect(Collectors.toList());
  }

  String getName();

  Optional<String> getAuthToken(Map<String, ?> credentials);

  Optional<String> getAuthToken();

  boolean authorize(String token);

  boolean authorize(String token, IAction action);
}
