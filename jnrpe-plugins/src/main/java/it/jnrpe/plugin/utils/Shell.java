package it.jnrpe.plugin.utils;

import it.jnrpe.utils.StreamManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ziccardi on 02/12/2016.
 */
public abstract class Shell {

  private static Shell INSTANCE;

  public enum OSTYPE {LINUX, MAC, WINDOWS};

  public final boolean isWindows() {
    return getOsType() == OSTYPE.WINDOWS;
  }

  public final boolean isLinux() {
    return getOsType() == OSTYPE.LINUX;
  }

  public final boolean isMac() {
    return getOsType() == OSTYPE.MAC;
  }

  public static Shell getInstance() {
    if (INSTANCE == null) {
      String os = System.getProperty("os.name").toLowerCase();

      if (os.contains("windows")) {
        INSTANCE = new WindowsShell();
      } else if (os.contains("mac")) {
        INSTANCE = new MacShell();
      } else {
        INSTANCE = new LinuxShell();
      }
    }

    return INSTANCE;
  }

  /**
   * Executes a system command with arguments and returns the output.
   *
   * @param command command to be executed
   * @param encoding encoding to be used
   * @return command output
   * @throws IOException on any error
   */
  public final String executeSystemCommandAndGetOutput(final String[] command, final String encoding) throws IOException {
    Process p = Runtime.getRuntime().exec(command);

    StreamManager sm = new StreamManager();
    try {
      InputStream input = sm.handle(p.getInputStream());
      StringBuffer lines = new StringBuffer();
      String line;

      BufferedReader in = (BufferedReader) sm.handle(new BufferedReader(new InputStreamReader(input, encoding)));
      while ((line = in.readLine()) != null) {
        lines.append(line).append('\n');
      }
      return lines.toString();
    } finally {
      sm.closeAll();
    }
  }

  public abstract OSTYPE getOsType();
}
