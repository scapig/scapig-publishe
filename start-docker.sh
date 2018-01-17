#!/bin/sh
SCRIPT=$(find . -type f -name scapig-publisher)
rm -f scapig-publisher*/RUNNING_PID
exec $SCRIPT -Dhttp.port=8040 -J-Xms128M -J-Xmx512m
