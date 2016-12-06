package it.jnrpe.plugin.utils;

/**
 * Created by ziccardi on 02/12/2016.
 */
public class LinuxShell extends Shell {
  @Override
  public OSTYPE getOsType() {
    return OSTYPE.LINUX;
  }
}
