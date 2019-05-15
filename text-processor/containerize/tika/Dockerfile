FROM alpine:latest
USER root

RUN apk update
RUN apk fetch openjdk8
RUN apk add openjdk8
RUN apk add wget
RUN wget -q http://apache.miloslavbrada.cz/tika/tika-server-1.20.jar

EXPOSE 9998

ENTRYPOINT ["java"]
CMD ["-jar", "tika-server-1.20.jar", "-h", "\"*\""]
