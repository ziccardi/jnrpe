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
package it.jnrpe.engine.services.command.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.ConfigurationManager;
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

public class CommandExecutionTest {
  private static final IJNRPEConfig config = ConfigurationManager.getConfig().orElseThrow();
  private static final Collection<INetworkListener> listeners = new ArrayList<>();

  private static final String HOST_ADDRESS = "host.testcontainers.internal";

  private static final GenericContainer container =
      new GenericContainer(DockerImageName.parse("ziccardi/jnrpe-test:latest"));

  private static void bind(IJNRPEConfig.Binding binding) {
    ServiceLoader.load(INetworkListener.class).stream()
        .map(ServiceLoader.Provider::get)
        .filter(l -> l.supportBinding(binding))
        .findFirst()
        .ifPresentOrElse(
            netListener -> {
              if (netListener.supportBinding(binding)) {
                EventManager.info(
                    "BindingConfigProxy on port %d using network provider named '%s'",
                    binding.port(), netListener.getName());
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
    config.getServer().bindings().forEach(CommandExecutionTest::bind);
    Testcontainers.exposeHostPorts(
        config.getServer().bindings().stream()
            .map(IJNRPEConfig.Binding::port)
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
  public void testCheckNRPEv2_SimpleCommand() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe", "-2", "-n", "-H", HOST_ADDRESS, "-p", "5668", "-c", "CMD_TEST_SIMPLE");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv2SSL_SimpleCommand() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe", "-2", "-H", HOST_ADDRESS, "-p", "5669", "-c", "CMD_TEST_SIMPLE");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3_SimpleCommand() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe", "-3", "-n", "-H", HOST_ADDRESS, "-p", "5668", "-c", "CMD_TEST_SIMPLE");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3SSL_SimpleCommand() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe", "-3", "-H", HOST_ADDRESS, "-p", "5669", "-c", "CMD_TEST_SIMPLE");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv4_SimpleCommand() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe", "-n", "-H", HOST_ADDRESS, "-p", "5668", "-c", "CMD_TEST_SIMPLE");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv4SSL_SimpleCommand() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe", "-H", HOST_ADDRESS, "-p", "5669", "-c", "CMD_TEST_SIMPLE");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv2_WithArgsNoSpaces() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe",
            "-2",
            "-n",
            "-H",
            HOST_ADDRESS,
            "-p",
            "5668",
            "-c",
            "CMD_TEST_WITHARGS",
            "-a",
            "This_is_a_test");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_WITHARGS - OK] - This_is_a_test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv2_WithArgsWithSpaces() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe",
            "-2",
            "-n",
            "-H",
            HOST_ADDRESS,
            "-p",
            "5668",
            "-c",
            "CMD_TEST_WITHARGS",
            "-a",
            "This is a test");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_WITHARGS - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv2_WithMultipleArgs() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe",
            "-2",
            "-n",
            "-H",
            HOST_ADDRESS,
            "-p",
            "5668",
            "-c",
            "CMD_TEST_WITHARG3",
            "-a",
            "This!is!a test");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_WITHARG3 - OK] - a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3_WithArgsNoSpaces() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe",
            "-3",
            "-n",
            "-H",
            HOST_ADDRESS,
            "-p",
            "5668",
            "-c",
            "CMD_TEST_WITHARGS",
            "-a",
            "This_is_a_test");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_WITHARGS - OK] - This_is_a_test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3_WithArgsWithSpaces() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe",
            "-3",
            "-n",
            "-H",
            HOST_ADDRESS,
            "-p",
            "5668",
            "-c",
            "CMD_TEST_WITHARGS",
            "-a",
            "This is a test");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_WITHARGS - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  @Test
  public void testCheckNRPEv3_WithMultipleArgs() throws Exception {
    var checkNrpeResult =
        container.execInContainer(
            "check_nrpe",
            "-3",
            "-n",
            "-H",
            HOST_ADDRESS,
            "-p",
            "5668",
            "-c",
            "CMD_TEST_WITHARG3",
            "-a",
            "This!is!a test");
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_WITHARG3 - OK] - a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }
}
