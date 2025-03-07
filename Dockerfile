# Usar una imagen base de OpenJDK 17
FROM eclipse-temurin:17-jdk AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar archivos necesarios para la compilación
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN chmod +x gradlew

# Copiar el código fuente y compilar la aplicación
COPY src src
RUN ./gradlew bootJar --no-daemon

# Segunda etapa: Imagen final para producción
FROM eclipse-temurin:17-jre

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto en el que se ejecuta Spring Boot
EXPOSE 8080

# Definir el comando de inicio
CMD ["java", "-jar", "app.jar"]
