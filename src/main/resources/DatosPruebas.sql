-- 1️⃣ Eliminar primero las cuentas (depende de `usuarios_detalles`)
DELETE FROM cuentas;

-- 2️⃣ Luego eliminar los usuarios
DELETE FROM usuarios_detalles;

-- 3️⃣ Insertar usuarios
INSERT INTO usuarios_detalles (id_number, name, last_name, phone_number, address, birth_date) VALUES
                                                                                                  ('123456789', 'Juan', 'Pérez', '3201234567', 'Calle 45 # 23-10', '1980-05-15'),
                                                                                                  ('987654321', 'María', 'Gómez', '3107654321', 'Carrera 12 # 56-78', '1985-09-20'),
                                                                                                  ('555666777', 'Carlos', 'Ramírez', '3219876543', 'Calle 8 # 10-15', '1992-03-10'),
                                                                                                  ('444333222', 'Laura', 'Sánchez', '3123456789', 'Avenida 30 # 5-20', '1995-07-25'),
                                                                                                  ('999888777', 'Pedro', 'López', '3145678901', 'Carrera 15 # 20-30', '1988-11-30');

-- 4️⃣ Insertar cuentas (con especialización para DOCTORES)
INSERT INTO cuentas (email, password, rol, status, user_id, tipo_doctor) VALUES
                                                                             ('juan.perez@clinica.com',  '$2a$10$ppr07MHNC8I3dIwkh1U5EO9t8KlZbPhtFVLbcgW0IqxRDO8GhLZBC', 'DOCTOR', 'ACTIVE', '123456789', 'ENDODONCISTA'),
                                                                             ('maria.gomez@clinica.com', '$2a$10$/XusW.ksxRAY4nlgfVhNxe4zP9ipjpMfSrGwNoZ47m9IeK0zu98XW', 'DOCTOR', 'ACTIVE', '987654321', 'ORTODONCISTA'),
                                                                             ('carlos.ramirez@gmail.com', '$2a$10$hza6RXjD3eU9RAz4cgiij.SIfQMIF1Bfw1Tm/RrvtYcMvGdubbqae', 'PACIENTE', 'ACTIVE', '555666777', NULL),
                                                                             ('laura.sanchez@hotmail.com', '$2a$10$5lXsYMkmi/zy4T9igTDvhugfgKM..Nadf50EoJzSfGepF3FLyF6uS', 'PACIENTE', 'INACTIVE', '444333222', NULL),
                                                                             ('pedro.lopez@yahoo.com', '$2a$10$QZqkVYlwo4vuzpe5YiBR6esUXQOpi5Gvz7JcLb9kWburDl6kNuX96', 'PACIENTE', 'ACTIVE', '999888777', NULL);

-- 5️⃣ Insertar citas (asociando doctores con la especialidad correcta)
INSERT INTO citas (paciente_id, odontologo_id, fecha_hora, estado, tipo_cita) VALUES
                                                                                  ('555666777', '123456789', '2024-04-01 10:00:00', 'PENDIENTE', 'TRATAMIENTO_DE_CONDUCTO'), -- Endodoncista
                                                                                  ('999888777', '987654321', '2024-04-02 15:30:00', 'CONFIRMADA', 'ORTODONCIA'); -- Ortodoncista