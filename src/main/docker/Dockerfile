FROM openjdk:latest

# set environment options
ENV JAVA_OPTS="-Xms128m -Xmx512m -XX:MaxMetaspaceSize=512m"

RUN mkdir -p /app
WORKDIR /app

COPY /app/application.jar application.jar
COPY /app/docker-entrypoint.sh docker-entrypoint.sh

# Set file permissions
RUN chmod +x /app/docker-entrypoint.sh

# Set start script as enrypoint
ENTRYPOINT ["./docker-entrypoint.sh"]