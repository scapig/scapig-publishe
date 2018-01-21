## scapig-publisher

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t scapig-publisher .
``

## Publishing
``
docker tag scapig-publisher scapig/scapig-publisher:VERSION
docker login
docker push scapig/scapig-publisher:VERSION
``

## Running
``
docker run -p9019:9019 -d scapig/scapig-publisher:VERSION
``
