FROM openjdk:11

RUN mkdir -p /user-service

WORKDIR /user-service

COPY ./target/*.jar user-service.jar

CMD ["java", "-jar", "user-service.jar"]