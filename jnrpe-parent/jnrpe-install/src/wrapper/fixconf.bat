@rem ***************************************************************************
@rem Copyright (c) 2007, 2014 Massimiliano Ziccardi
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem     http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem ***************************************************************************
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
