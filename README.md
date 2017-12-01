## tapi-publisher

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t tapi-publisher .
``

## Running
``
docker run -p8040:8040 -i -a stdin -a stdout -a stderr tapi-publisher sh start-docker.sh
``