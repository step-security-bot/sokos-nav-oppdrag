FROM ghcr.io/navikt/baseimages/temurin:17
COPY app/build/libs/*.jar ./
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"