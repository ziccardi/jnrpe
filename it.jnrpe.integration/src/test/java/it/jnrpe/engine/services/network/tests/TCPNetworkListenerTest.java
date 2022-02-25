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
package it.jnrpe.engine.services.network.tests;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.Binding;
import it.jnrpe.engine.services.config.ConfigurationManager;
import it.jnrpe.engine.services.config.JNRPEConfig;
import it.jnrpe.engine.services.network.INetworkListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class TCPNetworkListenerTest {
  private static final JNRPEConfig config = ConfigurationManager.getConfig().orElseThrow();
  private static final Collection<INetworkListener> listeners = new ArrayList<>();

  private static GenericContainer container =
      new GenericContainer(
          new ImageFromDockerfile("jnrpe", false)
              .withFileFromClasspath("Dockerfile", "Dockerfile"));

  private static void bind(Binding binding) {
    ServiceLoader.load(INetworkListener.class).stream()
        .map(ServiceLoader.Provider::get)
        .filter(l -> l.supportBinding(binding))
        .findFirst()
        .ifPresentOrElse(
            netListener -> {
              if (netListener.supportBinding(binding)) {
                EventManager.info(
                    "Binding on port %d using network provider named '%s'",
                    binding.getPort(), netListener.getName());
                netListener.bind(binding);
              }
              listeners.add(netListener);
            },
            () -> {
              EventManager.fatal("No network services has been found");
              throw new IllegalStateException("No Network Services found");
            });
  }

  @BeforeAll
  static void startJNRPE() throws Exception {
    config.getServer().getBindings().forEach(TCPNetworkListenerTest::bind);
    container.start();
  }

  @AfterAll
  static void stopJNRPE() {
    listeners.forEach(INetworkListener::shutdown);
    container.stop();
  }

  @Test
  public void testCheckNRPEv2() throws Exception {
    Container.ExecResult lsResult =
        container.execInContainer(
            "/nrpe-4.0.3/src/check_nrpe", "-2", "-n", "-H", "host.docker.internal", "-p", "5668");
    Assertions.assertEquals("JNRPE v3.0.0", lsResult.getStdout().trim());
    Assertions.assertEquals(0, lsResult.getExitCode());
  }

  @Test
  public void testCheckNRPEv3() throws Exception {
    Container.ExecResult lsResult =
        container.execInContainer(
            "/nrpe-4.0.3/src/check_nrpe", "-3", "-n", "-H", "host.docker.internal", "-p", "5668");
    Assertions.assertEquals("JNRPE v3.0.0", lsResult.getStdout().trim());
    Assertions.assertEquals(0, lsResult.getExitCode());
  }

  @Test
  public void testCheckNRPEv4() throws Exception {
    Container.ExecResult lsResult =
        container.execInContainer(
            "/nrpe-4.0.3/src/check_nrpe", "-n", "-H", "host.docker.internal", "-p", "5668");
    Assertions.assertEquals("JNRPE v3.0.0", lsResult.getStdout().trim());
    Assertions.assertEquals(0, lsResult.getExitCode());
  }

  @Test
  public void testCheckNRPEConnectionRefused() throws Exception {
    Container.ExecResult lsResult =
        container.execInContainer(
            "/nrpe-4.0.3/src/check_nrpe", "-n", "-H", "host.docker.internal", "-p", "5667");
    Assertions.assertEquals(
        "CHECK_NRPE: Receive header underflow - only 0 bytes received (4 expected).",
        lsResult.getStdout().trim());
    Assertions.assertEquals(3, lsResult.getExitCode());
  }
}