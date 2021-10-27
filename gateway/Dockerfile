FROM openjdk:12-alpine
COPY target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar
WORKDIR /app
ENV DB_NAME=opabs
CMD ["java", "-jar", "gateway.jar"]