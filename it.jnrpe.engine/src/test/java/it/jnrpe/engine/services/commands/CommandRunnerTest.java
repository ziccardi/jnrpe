/*******************************************************************************
 * Copyright (C) 2023, Massimiliano Ziccardi
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
package it.jnrpe.engine.services.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.jnrpe.engine.services.auth.IAuthService;
import it.jnrpe.engine.services.network.Status;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandRunnerTest {
  private CommandRunner commandRunner;
  private ICommandRepository commandRepository;
  private IAuthService authService;

  @BeforeEach
  public void setUp() {
    commandRepository = mock(ICommandRepository.class);
    authService = mock(IAuthService.class);
    commandRunner = new CommandRunner(commandRepository, authService);
  }

  @Test
  public void testExecute_WithAuthorizedToken_ShouldExecuteCommandAndReturnExecutionResult() {
    // Arrange
    String token = "valid_token";
    String commandName = "command1";
    String[] params = {"param1", "param2"};
    ICommandFactory commandDefinition = mock(ICommandFactory.class);
    ICommandInstance command = mock(ICommandInstance.class);
    ExecutionResult expectedResult = new ExecutionResult("result", Status.OK);
    when(authService.authorize(token)).thenReturn(true);
    when(commandRepository.getCommand(commandName)).thenReturn(Optional.of(commandDefinition));
    when(commandDefinition.instantiate(params)).thenReturn(command);
    when(command.execute()).thenReturn(expectedResult);
    // Act
    ExecutionResult actualResult = commandRunner.execute(token, commandName, params);
    // Assert
    assertEquals(expectedResult, actualResult);
    verify(authService).authorize(token);
    verify(commandRepository).getCommand(commandName);
    verify(commandDefinition).instantiate(params);
    verify(command).execute();
  }

  @Test
  public void testExecute_WithUnauthorizedToken_ShouldReturnUnauthorizedExecutionResult() {
    // Arrange
    String token = "invalid_token";
    String commandName = "command1";
    String[] params = {"param1", "param2"};
    when(authService.authorize(token)).thenReturn(false);
    // Act
    ExecutionResult actualResult = commandRunner.execute(token, commandName, params);
    // Assert
    assertEquals("Unauthorised [command1]", actualResult.getMessage());
    assertEquals(Status.UNKNOWN, actualResult.getStatus());
    verify(authService).authorize(token);
    verifyZeroInteractions(commandRepository);
  }

  @Test
  public void testExecute_WithUnknownCommandName_ShouldReturnErrorExecutionResult() {
    // Arrange
    String token = "valid_token";
    String commandName = "unknown_command";
    String[] params = {"param1", "param2"};
    when(authService.authorize(token)).thenReturn(true);
    when(commandRepository.getCommand(commandName)).thenReturn(Optional.empty());
    // Act
    ExecutionResult actualResult = commandRunner.execute(token, commandName, params);
    // Assert
    assertEquals(
        "[unknown_command - UNKNOWN] - Error executing command", actualResult.getMessage());
    assertEquals(Status.UNKNOWN, actualResult.getStatus());
    verify(authService).authorize(token);
    verify(commandRepository).getCommand(commandName);
  }
}
