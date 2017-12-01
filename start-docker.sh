#!/bin/sh
SCRIPT=$(find . -type f -name tapi-publisher)
exec $SCRIPT -Dhttp.port=8040
