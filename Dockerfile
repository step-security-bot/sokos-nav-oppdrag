FROM ghcr.io/navikt/baseimages/temurin:21@sha256:1401287bbb2262334140c116f5edcede0ae9ea8dd3eaedde5b6c7fb33ae741b9
COPY app/build/libs/*.jar ./
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"