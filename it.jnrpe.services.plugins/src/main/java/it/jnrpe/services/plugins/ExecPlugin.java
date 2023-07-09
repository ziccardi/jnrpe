/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
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
package it.jnrpe.services.plugins;

import static it.jnrpe.engine.events.EventManager.withException;
import static it.jnrpe.engine.events.EventManager.withMessage;

import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.network.Status;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.IPlugin;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.commons.text.StringTokenizer;
import org.apache.commons.text.matcher.StringMatcherFactory;

@CommandLine.Command(name = "EXEC")
public class ExecPlugin implements IPlugin {
  private static final String NAME = "EXEC";

  @CommandLine.Option(
      names = {"-e", "--executable"},
      required = true)
  private String executable;

  @CommandLine.Option(
      names = {"-a", "--args"},
      defaultValue = "")
  private String args;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public ExecutionResult execute() {
    File executableFile = new File(this.executable);

    if (!executableFile.exists()) {
      EventManager.error("File '%s' not found", executableFile.getAbsolutePath());
      return ExecutionResult.GENERAL_CHECK_ERROR;
    }

    if (!executableFile.canExecute()) {
      EventManager.error("File '%s' is not executable", executableFile.getAbsolutePath());
      return ExecutionResult.GENERAL_CHECK_ERROR;
    }

    var args =
        new StringTokenizer(
                this.args,
                StringMatcherFactory.INSTANCE.spaceMatcher(),
                StringMatcherFactory.INSTANCE.quoteMatcher())
            .getTokenArray();
    var command = new String[args.length + 1];
    command[0] = this.executable;
    System.arraycopy(args, 0, command, 1, args.length);
    try {
      Process p = Runtime.getRuntime().exec(command);

      try (BufferedReader br =
          new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {
        StringBuffer msg = new StringBuffer();

        for (String line = br.readLine(); line != null; line = br.readLine()) {
          if (msg.length() != 0) {
            msg.append(System.lineSeparator());
          }
          msg.append(line);
        }

        int returnCode = p.waitFor();

        return new ExecutionResult(msg.toString(), Status.values()[returnCode]);
      }
    } catch (Exception e) {
      EventManager.error(
          withMessage("Error executing '%s' plugin: %s", getName(), e.getMessage()),
          withException(e));
      return ExecutionResult.GENERAL_CHECK_ERROR;
    }
  }
}
