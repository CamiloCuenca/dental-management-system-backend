-- 1️⃣ Eliminar primero las cuentas (depende de `usuarios_detalles`)
DELETE FROM cuentas;

-- 2️⃣ Luego eliminar los usuarios
DELETE FROM usuarios_detalles;

-- 3️⃣ Insertar usuarios (Doctores)
INSERT INTO usuarios_detalles (id_number, name, last_name, phone_number, address, birth_date) VALUES
-- Doctores
('123456789', 'Juan', 'Pérez', '3201234567', 'Calle 45 # 23-10', '1980-05-15'),
('987654321', 'María', 'Gómez', '3107654321', 'Carrera 12 # 56-78', '1985-09-20'),
('111222333', 'Ana', 'Martínez', '3156789012', 'Avenida 25 # 10-15', '1982-03-25'),
('444555666', 'Roberto', 'López', '3123456789', 'Calle 30 # 5-20', '1987-07-30'),
('777888999', 'Carmen', 'Rodríguez', '3134567890', 'Carrera 8 # 15-25', '1983-11-12'),
('222333444', 'David', 'Sánchez', '3145678901', 'Avenida 40 # 20-30', '1986-04-18'),
('555666777', 'Laura', 'Torres', '3167890123', 'Calle 15 # 8-12', '1984-08-22'),
('888999000', 'Carlos', 'Ramírez', '3178901234', 'Carrera 20 # 30-40', '1981-12-05'),
('333444555', 'Sofia', 'García', '3189012345', 'Avenida 35 # 15-25', '1989-02-28'),
('666777888', 'Miguel', 'Hernández', '3190123456', 'Calle 50 # 25-35', '1988-06-15'),

-- Pacientes
('999888777', 'Pedro', 'López', '3145678901', 'Carrera 15 # 20-30', '1988-11-30'),
('111222333', 'Ana', 'Martínez', '3156789012', 'Avenida 25 # 10-15', '1990-03-25'),
('444555666', 'Roberto', 'López', '3123456789', 'Calle 30 # 5-20', '1992-07-30'),
('777888999', 'Carmen', 'Rodríguez', '3134567890', 'Carrera 8 # 15-25', '1995-11-12'),
('222333444', 'David', 'Sánchez', '3145678901', 'Avenida 40 # 20-30', '1993-04-18'),
('555666777', 'Laura', 'Torres', '3167890123', 'Calle 15 # 8-12', '1991-08-22'),
('888999000', 'Carlos', 'Ramírez', '3178901234', 'Carrera 20 # 30-40', '1994-12-05'),
('333444555', 'Sofia', 'García', '3189012345', 'Avenida 35 # 15-25', '1996-02-28'),
('666777888', 'Miguel', 'Hernández', '3190123456', 'Calle 50 # 25-35', '1997-06-15'),
('999000111', 'Isabella', 'Flores', '3201234567', 'Carrera 25 # 10-20', '1998-09-10'),
('222333444', 'Daniel', 'Morales', '3212345678', 'Avenida 45 # 30-40', '1999-01-15'),
('555666777', 'Valentina', 'Castro', '3223456789', 'Calle 60 # 35-45', '2000-03-20'),
('888999000', 'Sebastián', 'Ruiz', '3234567890', 'Carrera 30 # 15-25', '2001-05-25'),
('333444555', 'Camila', 'Ortiz', '3245678901', 'Avenida 50 # 20-30', '2002-07-30'),
('666777888', 'Matías', 'Silva', '3256789012', 'Calle 70 # 40-50', '2003-09-05');

-- 4️⃣ Insertar cuentas (con especialización para DOCTORES)
INSERT INTO cuentas (email, password, rol, status, user_id, tipo_doctor) VALUES
-- Doctores
('juan.perez@clinica.com', '$2a$10$ppr07MHNC8I3dIwkh1U5EO9t8KlZbPhtFVLbcgW0IqxRDO8GhLZBC', 'DOCTOR', 'ACTIVE', '123456789', 'ENDODONCISTA'),
('maria.gomez@clinica.com', '$2a$10$/XusW.ksxRAY4nlgfVhNxe4zP9ipjpMfSrGwNoZ47m9IeK0zu98XW', 'DOCTOR', 'ACTIVE', '987654321', 'ORTODONCISTA'),
('ana.martinez@clinica.com', '$2a$10$hza6RXjD3eU9RAz4cgiij.SIfQMIF1Bfw1Tm/RrvtYcMvGdubbqae', 'DOCTOR', 'ACTIVE', '111222333', 'CIRUJANO_ORAL'),
('roberto.lopez@clinica.com', '$2a$10$5lXsYMkmi/zy4T9igTDvhugfgKM..Nadf50EoJzSfGepF3FLyF6uS', 'DOCTOR', 'ACTIVE', '444555666', 'PERIODONCISTA'),
('carmen.rodriguez@clinica.com', '$2a$10$QZqkVYlwo4vuzpe5YiBR6esUXQOpi5Gvz7JcLb9kWburDl6kNuX96', 'DOCTOR', 'ACTIVE', '777888999', 'ODONTOPEDIATRA'),
('david.sanchez@clinica.com', '$2a$10$ppr07MHNC8I3dIwkh1U5EO9t8KlZbPhtFVLbcgW0IqxRDO8GhLZBC', 'DOCTOR', 'ACTIVE', '222333444', 'PROSTODONCISTA'),
('laura.torres@clinica.com', '$2a$10$/XusW.ksxRAY4nlgfVhNxe4zP9ipjpMfSrGwNoZ47m9IeK0zu98XW', 'DOCTOR', 'ACTIVE', '555666777', 'IMPLANTOLOGO'),
('carlos.ramirez@clinica.com', '$2a$10$hza6RXjD3eU9RAz4cgiij.SIfQMIF1Bfw1Tm/RrvtYcMvGdubbqae', 'DOCTOR', 'ACTIVE', '888999000', 'RADIOLOGO'),
('sofia.garcia@clinica.com', '$2a$10$5lXsYMkmi/zy4T9igTDvhugfgKM..Nadf50EoJzSfGepF3FLyF6uS', 'DOCTOR', 'ACTIVE', '333444555', 'PATOLOGO'),
('miguel.hernandez@clinica.com', '$2a$10$QZqkVYlwo4vuzpe5YiBR6esUXQOpi5Gvz7JcLb9kWburDl6kNuX96', 'DOCTOR', 'ACTIVE', '666777888', 'ODONTOGERIATRA'),

-- Pacientes
('pedro.lopez@email.com', '$2a$10$ppr07MHNC8I3dIwkh1U5EO9t8KlZbPhtFVLbcgW0IqxRDO8GhLZBC', 'PACIENTE', 'ACTIVE', '999888777', NULL),
('ana.martinez@email.com', '$2a$10$/XusW.ksxRAY4nlgfVhNxe4zP9ipjpMfSrGwNoZ47m9IeK0zu98XW', 'PACIENTE', 'ACTIVE', '111222333', NULL),
('roberto.lopez@email.com', '$2a$10$hza6RXjD3eU9RAz4cgiij.SIfQMIF1Bfw1Tm/RrvtYcMvGdubbqae', 'PACIENTE', 'ACTIVE', '444555666', NULL),
('carmen.rodriguez@email.com', '$2a$10$5lXsYMkmi/zy4T9igTDvhugfgKM..Nadf50EoJzSfGepF3FLyF6uS', 'PACIENTE', 'ACTIVE', '777888999', NULL),
('david.sanchez@email.com', '$2a$10$QZqkVYlwo4vuzpe5YiBR6esUXQOpi5Gvz7JcLb9kWburDl6kNuX96', 'PACIENTE', 'ACTIVE', '222333444', NULL),
('laura.torres@email.com', '$2a$10$ppr07MHNC8I3dIwkh1U5EO9t8KlZbPhtFVLbcgW0IqxRDO8GhLZBC', 'PACIENTE', 'ACTIVE', '555666777', NULL),
('carlos.ramirez@email.com', '$2a$10$/XusW.ksxRAY4nlgfVhNxe4zP9ipjpMfSrGwNoZ47m9IeK0zu98XW', 'PACIENTE', 'ACTIVE', '888999000', NULL),
('sofia.garcia@email.com', '$2a$10$hza6RXjD3eU9RAz4cgiij.SIfQMIF1Bfw1Tm/RrvtYcMvGdubbqae', 'PACIENTE', 'ACTIVE', '333444555', NULL),
('miguel.hernandez@email.com', '$2a$10$5lXsYMkmi/zy4T9igTDvhugfgKM..Nadf50EoJzSfGepF3FLyF6uS', 'PACIENTE', 'ACTIVE', '666777888', NULL),
('isabella.flores@email.com', '$2a$10$QZqkVYlwo4vuzpe5YiBR6esUXQOpi5Gvz7JcLb9kWburDl6kNuX96', 'PACIENTE', 'ACTIVE', '999000111', NULL),
('daniel.morales@email.com', '$2a$10$ppr07MHNC8I3dIwkh1U5EO9t8KlZbPhtFVLbcgW0IqxRDO8GhLZBC', 'PACIENTE', 'ACTIVE', '222333444', NULL),
('valentina.castro@email.com', '$2a$10$/XusW.ksxRAY4nlgfVhNxe4zP9ipjpMfSrGwNoZ47m9IeK0zu98XW', 'PACIENTE', 'ACTIVE', '555666777', NULL),
('sebastian.ruiz@email.com', '$2a$10$hza6RXjD3eU9RAz4cgiij.SIfQMIF1Bfw1Tm/RrvtYcMvGdubbqae', 'PACIENTE', 'ACTIVE', '888999000', NULL),
('camila.ortiz@email.com', '$2a$10$5lXsYMkmi/zy4T9igTDvhugfgKM..Nadf50EoJzSfGepF3FLyF6uS', 'PACIENTE', 'ACTIVE', '333444555', NULL),
('matias.silva@email.com', '$2a$10$QZqkVYlwo4vuzpe5YiBR6esUXQOpi5Gvz7JcLb9kWburDl6kNuX96', 'PACIENTE', 'ACTIVE', '666777888', NULL);

-- 5️⃣ Insertar citas (asociando doctores con la especialidad correcta)
INSERT INTO citas (paciente_id, odontologo_id, fecha_hora, estado, tipo_cita) VALUES
-- Citas con Endodoncista
('999888777', '123456789', '2024-04-01 10:00:00', 'PENDIENTE', 'TRATAMIENTO_DE_CONDUCTO'),
('111222333', '123456789', '2024-04-02 14:30:00', 'CONFIRMADA', 'TRATAMIENTO_DE_CONDUCTO'),

-- Citas con Ortodoncista
('444555666', '987654321', '2024-04-03 09:00:00', 'PENDIENTE', 'ORTODONCIA'),
('777888999', '987654321', '2024-04-04 15:00:00', 'CONFIRMADA', 'ORTODONCIA'),

-- Citas con Cirujano Oral
('222333444', '111222333', '2024-04-05 11:00:00', 'PENDIENTE', 'CIRUGIA_ORAL'),
('555666777', '111222333', '2024-04-06 16:00:00', 'CONFIRMADA', 'CIRUGIA_ORAL'),

-- Citas con Periodoncista
('888999000', '444555666', '2024-04-07 10:30:00', 'PENDIENTE', 'TRATAMIENTO_PERIODONTAL'),
('333444555', '444555666', '2024-04-08 14:00:00', 'CONFIRMADA', 'TRATAMIENTO_PERIODONTAL'),

-- Citas con Odontopediatra
('666777888', '777888999', '2024-04-09 09:30:00', 'PENDIENTE', 'ODONTOPEDIATRIA'),
('999000111', '777888999', '2024-04-10 15:30:00', 'CONFIRMADA', 'ODONTOPEDIATRIA'),

-- Citas con Prostodoncista
('222333444', '222333444', '2024-04-11 11:30:00', 'PENDIENTE', 'PROTESIS_DENTAL'),
('555666777', '222333444', '2024-04-12 16:30:00', 'CONFIRMADA', 'PROTESIS_DENTAL'),

-- Citas con Implantólogo
('888999000', '555666777', '2024-04-13 10:00:00', 'PENDIENTE', 'IMPLANTES_DENTALES'),
('333444555', '555666777', '2024-04-14 14:30:00', 'CONFIRMADA', 'IMPLANTES_DENTALES'),

-- Citas con Radiólogo
('666777888', '888999000', '2024-04-15 09:00:00', 'PENDIENTE', 'RADIOLOGIA_DENTAL'),
('999000111', '888999000', '2024-04-16 15:00:00', 'CONFIRMADA', 'RADIOLOGIA_DENTAL'),

-- Citas con Patólogo
('222333444', '333444555', '2024-04-17 11:00:00', 'PENDIENTE', 'PATOLOGIA_ORAL'),
('555666777', '333444555', '2024-04-18 16:00:00', 'CONFIRMADA', 'PATOLOGIA_ORAL'),

-- Citas con Odontogeriatra
('888999000', '666777888', '2024-04-19 10:30:00', 'PENDIENTE', 'ODONTOGERIATRIA'),
('333444555', '666777888', '2024-04-20 14:00:00', 'CONFIRMADA', 'ODONTOGERIATRIA');