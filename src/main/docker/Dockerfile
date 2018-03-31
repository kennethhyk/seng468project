FROM openjdk:latest

# set environment options
ENV JAVA_OPTS="-Xms1024m -Xmx2048m -XX:MaxPermSize=2048M -XX:PermSize=1024M"

RUN mkdir -p /app
WORKDIR /app

COPY /app/application.jar application.jar
COPY /app/docker-entrypoint.sh docker-entrypoint.sh

# Set file permissions
RUN chmod +x /app/docker-entrypoint.sh

# Set start script as enrypoint
ENTRYPOINT ["./docker-entrypoint.sh"]