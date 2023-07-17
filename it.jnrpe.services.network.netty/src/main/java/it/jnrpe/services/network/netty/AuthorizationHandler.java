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
package it.jnrpe.services.network.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.util.AttributeKey;
import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.auth.IAction;
import it.jnrpe.engine.services.auth.IAuthService;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

class AuthorizationHandler extends AbstractRemoteAddressFilter<InetSocketAddress> {
  private IAuthService auth;

  public AuthorizationHandler() {
    try {
      auth = IAuthService.getProviders().stream().findFirst().orElseThrow();
    } catch (NoSuchElementException nsee) {
      // TODO: log no auth found

      auth =
          new IAuthService() {
            @Override
            public String getName() {
              return "BLOCKEVERYTHING";
            }

            @Override
            public Optional<String> getAuthToken(Map<String, ?> credentials) {
              return Optional.empty();
            }

            @Override
            public Optional<String> getAuthToken() {
              return Optional.empty();
            }

            @Override
            public boolean authorize(String token) {
              return false;
            }

            @Override
            public boolean authorize(String token, IAction action) {
              return false;
            }
          };
    }
  }

  @Override
  protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {
    Channel userChannel = ctx.channel();
    InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
    String binding = sa.getHostString() + ":" + sa.getPort();
    var token = auth.getAuthToken(Map.of("BINDING", binding, "SRC_IP", remoteAddress.toString()));
    if (token.isEmpty()) {
      token =
          auth.getAuthToken(
              Map.of("BINDING", "0.0.0.0:" + sa.getPort(), "SRC_IP", remoteAddress.toString()));
    }

    if (token.isPresent()) {
      userChannel.attr(AttributeKey.valueOf("TOKEN")).set(token.get());
      return true;
    }
    EventManager.info("Denying connection from [%s]", remoteAddress);
    return false;
  }
}
