#!/bin/bash

LABEL="$1"
CMD="$2"
LOGFILE="$3"

line=$(printf '%*s' "50" | tr ' ' ".")

message=$(printf "%s %s" "$LABEL" "${line:${#LABEL}}")

eval "$CMD &> '$LOGFILE'" &
pid=$!

spin='-\|/'

i=0
while kill -0 $pid 2>/dev/null
do
  i=$(( (i+1) %4 ))
  printf "%s %s\r" "$message" "${spin:$i:1}"
  sleep .2
done

wait $pid && printf "%s OK\n" "$message" && exit 0

printf "%s FAIL\n" "$message"
cat "$LOGFILE"
exit 1
