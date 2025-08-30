/*******************************************************************************
 * Copyright (C) 2022, Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package it.jnrpe.engine.services.command.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.ConfigurationManager;
import it.jnrpe.engine.services.config.IJNRPEConfig;
import it.jnrpe.engine.services.network.INetworkListener;
import it.jnrpe.engine.services.network.Status;

class CommandExecutionTest {
  private static final IJNRPEConfig config = ConfigurationManager.getConfig().orElseThrow();
  private static final Collection<INetworkListener> listeners = new ArrayList<>();

  private static final String HOST_ADDRESS = "host.testcontainers.internal";

  @SuppressWarnings("resource") // Container lifecycle managed by @BeforeAll/@AfterAll
  private static final GenericContainer<?> container =
      new GenericContainer<>(DockerImageName.parse("ziccardi/jnrpe-test:latest"))
          .withCommand("sleep", "3600"); // Keep container running for tests

  private static void bind(IJNRPEConfig.Binding binding) {
    ServiceLoader.load(INetworkListener.class).stream().map(ServiceLoader.Provider::get)
        .filter(l -> l.supportBinding(binding)).findFirst().ifPresentOrElse(netListener -> {
          if (netListener.supportBinding(binding)) {
            EventManager.info("BindingConfigProxy on port %d using network provider named '%s'",
                binding.port(), netListener.getName());
            netListener.bind(binding);
          }
          listeners.add(netListener);
        }, () -> {
          EventManager.fatal("No network services has been found");
          throw new IllegalStateException("No Network Services found");
        });
  }

  @BeforeAll
  static void startJNRPE() {
    config.getServer().bindings().forEach(CommandExecutionTest::bind);
    Testcontainers.exposeHostPorts(config.getServer().bindings().stream()
        .map(IJNRPEConfig.Binding::port).mapToInt(p -> p).toArray());
    container.start();
  }

  @AfterAll
  static void stopJNRPE() {
    listeners.forEach(INetworkListener::shutdown);
    container.stop();
  }

  static Stream<Arguments> simpleCommandTestParameters() {
    return Stream.of(Arguments.of("NRPEv2", new String[] {"-2", "-n"}, "5668"),
        Arguments.of("NRPEv2SSL", new String[] {"-2"}, "5669"),
        Arguments.of("NRPEv3", new String[] {"-3", "-n"}, "5668"),
        Arguments.of("NRPEv3SSL", new String[] {"-3"}, "5669"),
        Arguments.of("NRPEv4", new String[] {"-n"}, "5668"),
        Arguments.of("NRPEv4SSL", new String[] {}, "5669"));
  }

  @ParameterizedTest(name = "testCheckNRPE_{0}_SimpleCommand")
  @MethodSource("simpleCommandTestParameters")
  void testSimpleCommand(String testName, String[] nrpeArgs, String port) throws Exception {
    var args = new ArrayList<String>();
    args.add("check_nrpe");
    for (String arg : nrpeArgs) {
      args.add(arg);
    }
    args.addAll(java.util.List.of("-H", HOST_ADDRESS, "-p", port, "-c", "CMD_TEST_SIMPLE"));

    var checkNrpeResult = container.execInContainer(args.toArray(String[]::new));
    assertThat(checkNrpeResult.getStdout().trim(), is("[CMD_TEST_SIMPLE - OK] - This is a test"));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }

  static Stream<Arguments> argumentTestParameters() {
    return Stream.of(
        Arguments.of("NRPEv2_WithArgsNoSpaces", new String[] {"-2", "-n"}, "CMD_TEST_WITHARGS",
            "This_is_a_test", "[CMD_TEST_WITHARGS - OK] - This_is_a_test"),
        Arguments.of("NRPEv2_WithArgsWithSpaces", new String[] {"-2", "-n"}, "CMD_TEST_WITHARGS",
            "This is a test", "[CMD_TEST_WITHARGS - OK] - This is a test"),
        Arguments.of("NRPEv2_WithMultipleArgs", new String[] {"-2", "-n"}, "CMD_TEST_WITHARG3",
            "This!is!a test", "[CMD_TEST_WITHARG3 - OK] - a test"),
        Arguments.of("NRPEv3_WithArgsNoSpaces", new String[] {"-3", "-n"}, "CMD_TEST_WITHARGS",
            "This_is_a_test", "[CMD_TEST_WITHARGS - OK] - This_is_a_test"),
        Arguments.of("NRPEv3_WithArgsWithSpaces", new String[] {"-3", "-n"}, "CMD_TEST_WITHARGS",
            "This is a test", "[CMD_TEST_WITHARGS - OK] - This is a test"),
        Arguments.of("NRPEv3_WithMultipleArgs", new String[] {"-3", "-n"}, "CMD_TEST_WITHARG3",
            "This!is!a test", "[CMD_TEST_WITHARG3 - OK] - a test"));
  }

  @ParameterizedTest(name = "testCheckNRPE_{0}")
  @MethodSource("argumentTestParameters")
  void testWithArguments(String testName, String[] nrpeArgs, String command, String argument,
      String expectedOutput) throws Exception {
    var args = new ArrayList<String>();
    args.add("check_nrpe");
    for (String arg : nrpeArgs) {
      args.add(arg);
    }
    args.addAll(java.util.List.of("-H", HOST_ADDRESS, "-p", "5668", "-c", command, "-a", argument));

    var checkNrpeResult = container.execInContainer(args.toArray(String[]::new));
    assertThat(checkNrpeResult.getStdout().trim(), is(expectedOutput));
    assertThat(checkNrpeResult.getExitCode(), is(Status.OK.ordinal()));
  }
}
