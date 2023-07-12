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

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.ConfigurationManager;
import it.jnrpe.engine.services.config.IBinding;
import it.jnrpe.engine.services.config.IJNRPEConfig;
import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.engine.services.network.Status;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class TCPNetworkListenerTest {
  private static final IJNRPEConfig config = ConfigurationManager.getConfig().orElseThrow();
  private static final Collection<INetworkListener> listeners = new ArrayList<>();

  private static final String HOST_ADDRESS = "host.testcontainers.internal";

  private static final GenericContainer container =
      new GenericContainer(DockerImageName.parse("ziccardi/jnrpe-test:latest"));

  private static void bind(IBinding binding) {
    ServiceLoader.load(INetworkListener.class).stream()
        .map(ServiceLoader.Provider::get)
        .filter(l -> l.supportBinding(binding))
        .findFirst()
        .ifPresentOrElse(
            netListener -> {
              if (netListener.supportBinding(binding)) {
                EventManager.info(
                    "BindingConfigProxy on port %d using network provider named '%s'",
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
  static void startJNRPE() {
    config.getServer().getBindings().forEach(TCPNetworkListenerTest::bind);
    Testcontainers.exposeHostPorts(
        config.getServer().getBindings().stream()
            .map(IBinding::getPort)
            .mapToInt(p -> p)
            .toArray());
    container.start();
  }

  @AfterAll
  static void stopJNRPE() {
    listeners.forEach(INetworkListener::shutdown);
    container.stop();
  }

  @Test
  public void testCheckNRPEv2() throws Exception {
    var checkNrpeResult =
        container.execInContainer("check_nrpe", "-2", "-n", "-H", HOST_ADDRESS, "-p", "5668");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv2SSL() throws Exception {
    var checkNrpeResult =
        container.execInContainer("check_nrpe", "-2", "-H", HOST_ADDRESS, "-p", "5669");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3() throws Exception {
    var checkNrpeResult =
        container.execInContainer("check_nrpe", "-3", "-n", "-H", HOST_ADDRESS, "-p", "5668");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3SSL() throws Exception {
    var checkNrpeResult =
        container.execInContainer("check_nrpe", "-3", "-H", HOST_ADDRESS, "-p", "5669");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv4() throws Exception {
    var checkNrpeResult =
        container.execInContainer("check_nrpe", "-n", "-H", HOST_ADDRESS, "-p", "5668");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv4SSL() throws Exception {
    var checkNrpeResult = container.execInContainer("check_nrpe", "-H", HOST_ADDRESS, "-p", "5669");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEMultipleRequests() throws Exception {
    var checkNrpeResult = container.execInContainer("check_nrpe", "-H", HOST_ADDRESS, "-p", "5669");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));

    checkNrpeResult = container.execInContainer("check_nrpe", "-H", HOST_ADDRESS, "-p", "5669");
    assertThat(checkNrpeResult.getStdout().trim(), is("JNRPE v3.0.0"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testInvalidPacket() throws Exception {
    // We simulate an invalid packet connecting an SSL client with a non SSL server
    var checkNrpeResult = container.execInContainer("check_nrpe", "-H", HOST_ADDRESS, "-p", "5668");
    assertThat(
        checkNrpeResult.getStdout(),
        anyOf(
            startsWith("CHECK_NRPE: Error - Could not connect to"),
            startsWith("CHECK_NRPE STATE CRITICAL: Socket timeout")));
    assertThat(checkNrpeResult.getExitCode(), is(Status.CRITICAL.ordinal()));
  }

  @Test
  public void testCheckNRPEConnectionRefused() throws Exception {
    var checkNrpeResult =
        container.execInContainer("check_nrpe", "-n", "-H", HOST_ADDRESS, "-p", "5667");
    assertThat(
        checkNrpeResult.getStdout().trim(),
        is("CHECK_NRPE: Receive header underflow - only 0 bytes received (4 expected)."));
    assertThat(checkNrpeResult.getExitCode(), is(Status.UNKNOWN.ordinal()));
  }
}
