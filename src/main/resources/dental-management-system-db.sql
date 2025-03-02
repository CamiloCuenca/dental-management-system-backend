CREATE DATABASE  IF NOT EXISTS `dental-management-system-db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dental-management-system-db`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: dental-management-system-db
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `citas`
--

DROP TABLE IF EXISTS `citas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `citas` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `fecha_hora` datetime(6) NOT NULL,
                         `odontologo_id` varchar(20) NOT NULL,
                         `paciente_id` varchar(20) NOT NULL,
                         `estado` enum('CANCELADA','COMPLETADA','CONFIRMADA','PENDIENTE') NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `citas`
--

LOCK TABLES `citas` WRITE;
/*!40000 ALTER TABLE `citas` DISABLE KEYS */;
INSERT INTO `citas` VALUES (1,'2025-03-10 09:00:00.000000','123456789','555666777','CONFIRMADA'),(2,'2025-03-12 10:30:00.000000','987654321','444333222','PENDIENTE'),(3,'2025-03-15 14:00:00.000000','123456789','999888777','CANCELADA'),(4,'2025-03-20 16:45:00.000000','987654321','555666777','COMPLETADA'),(5,'2024-02-29 15:30:00.000000','123456789','555666777','PENDIENTE');
/*!40000 ALTER TABLE `citas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuentas`
--

DROP TABLE IF EXISTS `cuentas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuentas` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `fecha_registro` datetime(6) NOT NULL,
                           `codigo_activacion` varchar(10) DEFAULT NULL,
                           `user_id` varchar(20) DEFAULT NULL,
                           `email` varchar(100) NOT NULL,
                           `user_password` varchar(255) NOT NULL,
                           `estado` enum('ACTIVE','ELIMINATED','INACTIVE') NOT NULL,
                           `rol` enum('ADMINISTRATOR','DOCTOR','PACIENTE') NOT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `UKgys3uxxv6wwfj8fjmjm8g7mfo` (`email`),
                           UNIQUE KEY `UKku9urpj2pw90mwkpl29r5d40g` (`codigo_activacion`),
                           UNIQUE KEY `UKhawmmtk5j1rwxk9ve9qch85q0` (`user_id`),
                           CONSTRAINT `FKjx2ldifulgf74ikmcg89idhbw` FOREIGN KEY (`user_id`) REFERENCES `usuarios_detalles` (`id_number`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuentas`
--

LOCK TABLES `cuentas` WRITE;
/*!40000 ALTER TABLE `cuentas` DISABLE KEYS */;
INSERT INTO `cuentas` VALUES (1,'2025-03-02 00:03:40.000000','ABC123','123456789','juan.perez@clinica.com','password123','ACTIVE','DOCTOR'),(2,'2025-03-02 00:03:40.000000','DEF456','987654321','maria.gomez@clinica.com','password123','ACTIVE','DOCTOR'),(3,'2025-03-02 00:03:40.000000','GHI789','555666777','carlos.ramirez@gmail.com','password123','ACTIVE','PACIENTE'),(4,'2025-03-02 00:03:40.000000','JKL012','444333222','laura.sanchez@hotmail.com','password123','INACTIVE','PACIENTE'),(5,'2025-03-02 00:03:40.000000','MNO345','999888777','pedro.lopez@yahoo.com','password123','ACTIVE','PACIENTE');
/*!40000 ALTER TABLE `cuentas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historiales_medicos`
--

DROP TABLE IF EXISTS `historiales_medicos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historiales_medicos` (
                                       `fecha` date NOT NULL,
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `odontologo_id` varchar(20) NOT NULL,
                                       `paciente_id` varchar(20) NOT NULL,
                                       `descripcion` text NOT NULL,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historiales_medicos`
--

LOCK TABLES `historiales_medicos` WRITE;
/*!40000 ALTER TABLE `historiales_medicos` DISABLE KEYS */;
INSERT INTO `historiales_medicos` VALUES ('2025-02-15',1,'123456789','555666777','Extracción de muela del juicio'),('2025-02-20',2,'987654321','444333222','Limpieza dental y revisión de caries'),('2025-02-28',3,'123456789','999888777','Tratamiento de conducto en molar superior derecho');
/*!40000 ALTER TABLE `historiales_medicos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `nombre` varchar(100) NOT NULL,
                              `cantidad` int NOT NULL,
                              `unidad` varchar(50) DEFAULT NULL,
                              `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios_detalles`
--

DROP TABLE IF EXISTS `usuarios_detalles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios_detalles` (
                                     `id_number` varchar(20) NOT NULL,
                                     `telefono` varchar(20) DEFAULT NULL,
                                     `nombre` varchar(100) NOT NULL,
                                     `direccion` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`id_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios_detalles`
--

LOCK TABLES `usuarios_detalles` WRITE;
/*!40000 ALTER TABLE `usuarios_detalles` DISABLE KEYS */;
INSERT INTO `usuarios_detalles` VALUES ('123456789','3201234567','Dr. Juan Pérez','Calle 45 # 23-10'),('444333222','3123456789','Laura Sánchez','Avenida 30 # 5-20'),('555666777','3219876543','Carlos Ramírez','Calle 8 # 10-15'),('987654321','3107654321','Dra. María Gómez','Carrera 12 # 56-78'),('999888777','3145678901','Pedro López','Carrera 15 # 20-30');
/*!40000 ALTER TABLE `usuarios_detalles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-02  0:21:34
