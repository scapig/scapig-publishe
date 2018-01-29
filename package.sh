#!/bin/sh
sbt universal:package-zip-tarball
docker build -t scapig-publisher .
docker tag scapig-publisher scapig/scapig-publisher
docker push scapig/scapig-publisher
