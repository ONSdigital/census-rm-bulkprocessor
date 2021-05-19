FROM openjdk:11-slim
CMD ["/usr/local/openjdk-11/bin/java", "-jar", "/opt/census-rm-bulkprocessor.jar"]

RUN groupadd --gid 999 bulkprocessor && \
    useradd --create-home --system --uid 999 --gid bulkprocessor bulkprocessor

RUN apt-get update && \
apt-get -yq install curl && \
apt-get -yq clean && \
rm -rf /var/lib/apt/lists/*

USER bulkprocessor

ARG JAR_FILE=census-rm-bulkprocessor*.jar
COPY target/$JAR_FILE /opt/census-rm-bulkprocessor.jar
