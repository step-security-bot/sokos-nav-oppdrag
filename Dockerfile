FROM ghcr.io/navikt/baseimages/temurin:17
COPY app/build/libs/*.jar app.jar
COPY .initscript /init-scripts
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"