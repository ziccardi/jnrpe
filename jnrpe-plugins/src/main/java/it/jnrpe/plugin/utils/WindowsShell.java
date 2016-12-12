package it.jnrpe.plugin.utils;

/**
 * Created by ziccardi on 02/12/2016.
 */
public class WindowsShell extends Shell {
  @Override
  public OSTYPE getOsType() {
    return OSTYPE.WINDOWS;
  }

  /**
   *
   * Check if name of process is a windows idle process.
   *
   * @param proc process to be checked
   * @return true if idle process, false otherwise
   */
  public static boolean isIdleProc(final String proc) {
    String process = proc.trim().toLowerCase();
    if ("system idle process".equals(process) || process.contains("inactiv") || "system".equals(process)) {
      return true;
    }
    return false;
  }
}
