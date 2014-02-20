#!/bin/bash

index=0

for libraryname in `find "$INSTALL_PATH/lib" -type f -printf "%f\n"`
do
  let "index += 1"
  echo "wrapper.java.classpath.$index=lib/$libraryname" >> "$INSTALL_PATH/wrapper/etc/wrapper.conf"
done

let "index += 1"
echo "wrapper.java.classpath.$index=etc" >> "$INSTALL_PATH/wrapper/etc/wrapper.conf"
