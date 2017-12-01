FROM openjdk:8

COPY target/universal/tapi-publisher-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf tapi-publisher-*.tgz

EXPOSE 8040