FROM openjdk:8-jdk-alpine
RUN mkdir /data
RUN mkdir /qdar
WORKDIR /qdar
COPY ./qdar-cli.jar .
COPY run.sh .
RUN chmod +x run.sh
ENTRYPOINT [ "sh", "/qdar/run.sh" ]