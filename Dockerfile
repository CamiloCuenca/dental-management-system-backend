# Build stage
FROM gradle:8.8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Otorgar permisos de ejecución a gradlew
RUN chmod +x ./gradlew

RUN ./gradlew clean bootJar --no-daemon

# Package stage
FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /home/gradle/src/${JAR_FILE} app.jar

# Render/Koyeb establecen la variable PORT automáticamente
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
