-- Tabla de Usuarios (Pacientes, Odontólogos, Administradores)
CREATE TABLE Usuarios (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          telefono VARCHAR(20),
                          direccion VARCHAR(255),
                          contraseña VARCHAR(255) NOT NULL,
                          rol ENUM('PACIENTE', 'ODONTOLOGO', 'ADMINISTRADOR') NOT NULL
);

-- Tabla de Historiales Médicos
CREATE TABLE Historiales_Medicos (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     paciente_id INT NOT NULL,
                                     odontologo_id INT NOT NULL,
                                     fecha DATE NOT NULL,
                                     descripcion TEXT,
                                     FOREIGN KEY (paciente_id) REFERENCES Usuarios(id) ON DELETE CASCADE,
                                     FOREIGN KEY (odontologo_id) REFERENCES Usuarios(id) ON DELETE CASCADE
);

-- Tabla de Citas
CREATE TABLE Citas (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       paciente_id INT NOT NULL,
                       odontologo_id INT NOT NULL,
                       fecha_hora DATETIME NOT NULL,
                       estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA') DEFAULT 'PENDIENTE',
                       FOREIGN KEY (paciente_id) REFERENCES Usuarios(id) ON DELETE CASCADE,
                       FOREIGN KEY (odontologo_id) REFERENCES Usuarios(id) ON DELETE CASCADE
);

-- Tabla de Inventario Odontológico
CREATE TABLE Inventario (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            cantidad INT NOT NULL,
                            unidad VARCHAR(50),
                            fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Notificaciones
CREATE TABLE Notificaciones (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                usuario_id INT NOT NULL,
                                mensaje TEXT NOT NULL,
                                fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (usuario_id) REFERENCES Usuarios(id) ON DELETE CASCADE
);
