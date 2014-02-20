@echo off
setlocal enableextensions enabledelayedexpansion
set /A count=0

@echo off
FOR /f %%G IN ('dir "$INSTALL_PATH/lib" /b') DO (
  set /A count+=1
  echo wrapper.java.classpath.!count!=lib/%%G >> "$INSTALL_PATH\wrapper\etc\wrapper.conf"
)


set /A count+=1
echo wrapper.java.classpath.!count!=etc >> "$INSTALL_PATH\wrapper\etc\wrapper.conf"
