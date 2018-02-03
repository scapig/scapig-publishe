#!/bin/sh
SCRIPT=$(find . -type f -name scapig-publisher)
rm -f scapig-publisher*/RUNNING_PID
exec $SCRIPT -Dhttp.port=9019 $JAVA_OPTS -J-Xms16M -J-Xmx64m
