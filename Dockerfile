#
# Build stage
#
FROM gradle:8.8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew clean bootJar --no-daemon

#
# Package stage
#
FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /home/gradle/src/${JAR_FILE} app.jar
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "/app.jar"]