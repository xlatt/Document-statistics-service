FROM alpine:latest AS build
USER root

RUN apk update
RUN apk fetch gradle
RUN apk add gradle
RUN apk fetch openjdk8
RUN apk add openjdk8
RUN apk add wget

COPY ./text-processor .
RUN gradle build
RUN gradle fatJar

FROM openjdk:8-jre
COPY --from=build /build/libs/text-processor-1.0-SNAPSHOT-all.jar .

ARG tika-url=http://172.17.0.6:9998
ARG db-url=mongodb://172.17.0.10:27017

EXPOSE 4567

ENTRYPOINT ["java"]
CMD ["-jar", "text-processor-1.0-SNAPSHOT-all.jar", "-t", "$tika-url", "-db", "$db-url"]
