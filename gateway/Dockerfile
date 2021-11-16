FROM openjdk:18-jdk-alpine3.14
COPY target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar
WORKDIR /app
ENV DB_NAME=opabs
CMD ["java", "-jar", "gateway.jar"]