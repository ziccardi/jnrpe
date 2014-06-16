#*******************************************************************************
# Copyright (c) 2007, 2014 Massimiliano Ziccardi
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#*******************************************************************************
#!/bin/bash

index=0

for libraryname in `find "$INSTALL_PATH/lib" -type f -printf "%f\n"`
do
  let "index += 1"
  echo "wrapper.java.classpath.$index=lib/$libraryname" >> "$INSTALL_PATH/wrapper/etc/wrapper.conf"
done

let "index += 1"
echo "wrapper.java.classpath.$index=etc" >> "$INSTALL_PATH/wrapper/etc/wrapper.conf"
