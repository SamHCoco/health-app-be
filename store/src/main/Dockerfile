FROM openjdk:11

RUN mkdir -p /store-service

WORKDIR /store-service

COPY target/*.jar store-service.jar

CMD ["java", "-jar", "store-service.jar"]