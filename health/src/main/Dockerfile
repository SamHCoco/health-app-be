FROM openjdk:11

RUN mkdir -p /health-service

WORKDIR /health-service

COPY target/*.jar health-service.jar

CMD ["java", "-jar", "health-service.jar"]