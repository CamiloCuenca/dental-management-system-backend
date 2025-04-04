-- Desactivar temporalmente restricciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar tablas existentes
DELETE FROM citas;
DELETE FROM disponibilidad_doctor;
DELETE FROM doctor_especialidad;
DELETE FROM tipos_cita;
DELETE FROM especialidades;
DELETE FROM cuentas;
DELETE FROM users;

-- Reactivar restricciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- Insertar Especialidades
INSERT INTO especialidades (nombre, descripcion, duracion_promedio, activo, codigo_interno, nivel_complejidad, fecha_creacion) VALUES
    ('Odontología General', 'Servicios básicos de odontología', 30, true, 'ODG001', 1, NOW()),
    ('Higiene Dental', 'Limpieza y mantenimiento dental', 45, true, 'HIG001', 1, NOW()),
    ('Cirugía Oral', 'Procedimientos quirúrgicos orales', 60, true, 'CIR001', 3, NOW()),
    ('Endodoncia', 'Tratamientos de conducto', 90, true, 'END001', 3, NOW()),
    ('Ortodoncia', 'Tratamientos de alineación dental', 45, true, 'ORT001', 2, NOW()),
    ('Periodoncia', 'Tratamiento de encías e implantes', 60, true, 'PER001', 2, NOW()),
    ('Odontología Estética', 'Procedimientos estéticos dentales', 60, true, 'EST001', 2, NOW());

-- Insertar Tipos de Cita
INSERT INTO tipos_cita (nombre, descripcion, especialidad_id, duracion_minutos, requiere_historial, prioridad, codigo_interno, activo, fecha_creacion) VALUES
    ('Consulta General', 'Revisión dental general', 1, 30, false, 1, 'CG001', true, NOW()),
    ('Limpieza Dental', 'Limpieza dental profesional', 2, 45, false, 1, 'LD001', true, NOW()),
    ('Extracción Dental', 'Extracción de piezas dentales', 3, 60, true, 2, 'ED001', true, NOW()),
    ('Tratamiento de Conducto', 'Endodoncia', 4, 90, true, 2, 'TC001', true, NOW()),
    ('Consulta Ortodoncia', 'Revisión de tratamiento ortodóntico', 5, 45, true, 1, 'CO001', true, NOW()),
    ('Implante Dental', 'Colocación de implantes', 6, 120, true, 3, 'ID001', true, NOW()),
    ('Blanqueamiento', 'Blanqueamiento dental profesional', 7, 60, false, 1, 'BD001', true, NOW());

-- Insertar usuarios y sus cuentas
INSERT INTO users (id_number, name, last_name, phone_number, address, birth_date)
VALUES
    -- Doctores
    ('111111111', 'Andrés', 'Torres', '3001111111', 'Calle 1 # 2-3', '1980-01-01'),
    ('222222222', 'Beatriz', 'Martínez', '3012222222', 'Carrera 4 # 5-6', '1982-02-02'),
    -- Pacientes
    ('333333333', 'Carlos', 'Pérez', '3023333333', 'Avenida 7 # 8-9', '1990-03-03'),
    ('444444444', 'Diana', 'López', '3034444444', 'Calle 10 # 11-12', '1992-04-04');

INSERT INTO cuentas (email, password, rol, status, user_id, created_at)
VALUES
    ('andres.torres@clinica.com', '$2a$10$XT7QRuo1TuqmcGihg5CN1uFAhL5nat3bqamFTGAgefp3VwP/DMqVe', 'DOCTOR', 'ACTIVE', '111111111', NOW()),
    ('beatriz.martinez@clinica.com', '$2a$10$hZDofY1Sc.fBvGnE8X2Q4eSFI5JBML/iB54r0C0V.Yz7U0JIALIaq', 'DOCTOR', 'ACTIVE', '222222222', NOW()),
    ('carlos.perez@email.com', '$2a$10$SUq/4RlcDDmYtfxwWadCtOfrpZ31lXhOhlINr5SiBI4EtfW9y/CJ6', 'PACIENTE', 'ACTIVE', '333333333', NOW()),
    ('diana.lopez@email.com', '$2a$10$yY5g1sLXRa6HexmHa0AS7.ndIrL3i3CRdeLFv/Y7cwPDWw.hZa.c6', 'PACIENTE', 'ACTIVE', '444444444', NOW());

-- Asignar especialidades a doctores
INSERT INTO doctor_especialidad (doctor_id, especialidad_id) VALUES
    ('111111111', 1), -- Andrés: Odontología General
    ('111111111', 2), -- Andrés: Higiene Dental
    ('222222222', 3), -- Beatriz: Cirugía Oral
    ('222222222', 4); -- Beatriz: Endodoncia

-- Insertar disponibilidad de doctores
INSERT INTO disponibilidad_doctor (doctor_id, dia_semana, hora_inicio, hora_fin, estado, intervalo_citas, fecha_creacion) VALUES
    ('111111111', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 30, NOW()),
    ('111111111', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 30, NOW()),
    ('222222222', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 45, NOW()),
    ('222222222', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 45, NOW());

-- Insertar citas (asegurándose de que los IDs de tipo_cita existan)
-- Primero, verificar los IDs de tipos_cita
SELECT id, nombre FROM tipos_cita;

-- Luego insertar citas con los IDs correctos
INSERT INTO citas (paciente_id, odontologo_id, fecha_hora, estado, tipo_cita_id) VALUES
    ('333333333', '111111111', '2024-04-01 09:00:00', 'PENDIENTE', 1),
    ('444444444', '111111111', '2024-04-01 10:00:00', 'CONFIRMADA', 2),
    ('333333333', '222222222', '2024-04-02 09:00:00', 'PENDIENTE', 3),
    ('444444444', '222222222', '2024-04-02 10:30:00', 'CONFIRMADA', 4); 