#!/bin/sh

DEFAULT_CARD=/dev/dri/card0
DEFAULT_TIMEOUT=5

if [ "$1"x = x ]; then
    card=${DEFAULT_CARD}
else
    card=$1
fi

if [ "$2"x = x ]; then
    timeout=${DEFAULT_TIMEOUT}
else
    timeout=$1
fi

ctr=0
while true; do
    echo "ctr=${ctr}"
    if [ -e ${card} ]; then break; fi;
    sleep 1
    ctr=$(echo ${ctr} + 1 | bc)
    if [ ${ctr} -ge ${timeout} ]; then
      echo "Timeout waiting for ${card}"
      exit 1
    fi
done
