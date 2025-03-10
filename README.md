# 🦷 Dental Management System - Backend

Sistema de gestión para clínicas odontológicas que permite la digitalización de historiales médicos, control de inventario y agendamiento automatizado de citas. Este repositorio contiene el backend desarrollado con Spring Boot y MySQL.

## 🚀 Tecnologías Utilizadas

- **Lenguaje:** Java 17
- **Framework:** Spring Boot 3.4.3
- **Base de Datos:** MySQL
- **Seguridad:** Spring Security, JSON Web Token (JWT)
- **Correo Electrónico:** Simple Java Mail
- **Dependencias Principales:** Lombok, JPA, WebClient
- **Herramienta de Construcción:** Gradle

## 📦 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/CamiloCuenca/dental-management-system-backend.git
cd dental-management-system-backend
```

### 2. Configurar la base de datos en `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dental_management
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

### 3. Construir el proyecto con Gradle
```bash
./gradlew build
```

### 4. Ejecutar el servidor
```bash
./gradlew bootRun
```

## 📂 Estructura del Proyecto
```
📦 dental-management-system-backend
├── 📂 src/main/java/edu/uniquindio/dentalmanagementsystembackend
│   ├── 📂 config
│   ├── 📂 controller
│   ├── 📂 dto
│   ├── 📂 entity
│   ├── 📂 Enum
│   ├── 📂 exception
│   ├── 📂 repository
│   ├── 📂 security
│   ├── 📂 service
│   │   ├── 📂 impl
│   │   ├── 📂 Interfaces
├── 📂 src/main/resources
│   ├── application.properties
├── build.gradle
├── README.md
```

## ✨ Características Principales

✅ Gestión de historiales médicos digitales  
✅ Control de inventario odontológico  
✅ Agendamiento automatizado de citas  
✅ Seguridad con JWT y Spring Security  
✅ Envío de correos con Simple Java Mail  
✅ Documentación con Spring REST Docs  

## 📌 Dependencias Backend
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.3'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'org.asciidoctor.jvm.convert' version '3.3.2'
}

group = 'edu.uniquindio'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.mysql:mysql-connector-j'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    implementation 'org.simplejavamail:simple-java-mail:8.2.0'
    implementation 'org.springframework.security:spring-security-crypto:6.3.3'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
}
```

## 🛠 Contribuciones

Si deseas contribuir al proyecto:

1. Haz un fork del repositorio.
2. Crea una rama con tu nueva funcionalidad: `git checkout -b feature/nueva-funcionalidad`
3. Realiza cambios y haz commit siguiendo la convención de mensajes: `feat: added new appointment system`
4. Sube tu rama y crea un Pull Request.


