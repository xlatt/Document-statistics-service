FROM alpine:latest AS build
USER root

RUN apk update
RUN apk fetch gradle
RUN apk add gradle
RUN apk fetch openjdk8
RUN apk add openjdk8
RUN apk add wget

COPY text-processor .
gradle fatJar

FROM openjdk:8-jre
COPY --from=build text-processor-1.0.jar .
ENTRYPOINT ["java"]
CMD ["-jar", "text-processor-1.0.jar"]
