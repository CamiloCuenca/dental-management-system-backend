-- Poblar la tabla usuarios_detalles
INSERT INTO usuarios_detalles (id_number, telefono, nombre, direccion) VALUES
                                                                           ('123456789', '3201234567', 'Dr. Juan Pérez', 'Calle 45 # 23-10'),
                                                                           ('987654321', '3107654321', 'Dra. María Gómez', 'Carrera 12 # 56-78'),
                                                                           ('555666777', '3219876543', 'Carlos Ramírez', 'Calle 8 # 10-15'),
                                                                           ('444333222', '3123456789', 'Laura Sánchez', 'Avenida 30 # 5-20'),
                                                                           ('999888777', '3145678901', 'Pedro López', 'Carrera 15 # 20-30');

-- Poblar la tabla cuentas
INSERT INTO cuentas (fecha_registro, codigo_activacion, user_id, email, user_password, estado, rol) VALUES
                                                                                                        (NOW(), 'ABC123', '123456789', 'juan.perez@clinica.com', 'password123', 'ACTIVE', 'DOCTOR'),
                                                                                                        (NOW(), 'DEF456', '987654321', 'maria.gomez@clinica.com', 'password123', 'ACTIVE', 'DOCTOR'),
                                                                                                        (NOW(), 'GHI789', '555666777', 'carlos.ramirez@gmail.com', 'password123', 'ACTIVE', 'PACIENTE'),
                                                                                                        (NOW(), 'JKL012', '444333222', 'laura.sanchez@hotmail.com', 'password123', 'INACTIVE', 'PACIENTE'),
                                                                                                        (NOW(), 'MNO345', '999888777', 'pedro.lopez@yahoo.com', 'password123', 'ACTIVE', 'PACIENTE');

-- Poblar la tabla citas
INSERT INTO citas (fecha_hora, odontologo_id, paciente_id, estado) VALUES
                                                                       ('2025-03-10 09:00:00', '123456789', '555666777', 'CONFIRMADA'),
                                                                       ('2025-03-12 10:30:00', '987654321', '444333222', 'PENDIENTE'),
                                                                       ('2025-03-15 14:00:00', '123456789', '999888777', 'CANCELADA'),
                                                                       ('2025-03-20 16:45:00', '987654321', '555666777', 'COMPLETADA');

-- Poblar la tabla historiales_medicos
INSERT INTO historiales_medicos (fecha, odontologo_id, paciente_id, descripcion) VALUES
                                                                                     ('2025-02-15', '123456789', '555666777', 'Extracción de muela del juicio'),
                                                                                     ('2025-02-20', '987654321', '444333222', 'Limpieza dental y revisión de caries'),
                                                                                     ('2025-02-28', '123456789', '999888777', 'Tratamiento de conducto en molar superior derecho');