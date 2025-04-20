-- Desactivar temporalmente restricciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar tablas existentes

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
    -- Doctores Odontología General
    ('111111111', 'Andrés', 'Torres', '3001111111', 'Calle 1 # 2-3', '1980-01-01'),
    ('222222222', 'Beatriz', 'Martínez', '3012222222', 'Carrera 4 # 5-6', '1982-02-02'),
    -- Doctores Higiene Dental
    ('333333333', 'Carlos', 'García', '3023333333', 'Avenida 7 # 8-9', '1985-03-03'),
    ('444444444', 'Diana', 'López', '3034444444', 'Calle 10 # 11-12', '1983-04-04'),
    -- Doctores Cirugía Oral
    ('555555555', 'Eduardo', 'Ramírez', '3045555555', 'Carrera 13 # 14-15', '1979-05-05'),
    ('666666666', 'Fernanda', 'Sánchez', '3056666666', 'Avenida 16 # 17-18', '1981-06-06'),
    -- Doctores Endodoncia
    ('777777777', 'Gabriel', 'Pérez', '3067777777', 'Calle 19 # 20-21', '1984-07-07'),
    ('888888888', 'Héctor', 'Gómez', '3078888888', 'Carrera 22 # 23-24', '1982-08-08'),
    -- Doctores Ortodoncia
    ('999999999', 'Isabel', 'Castro', '3089999999', 'Avenida 25 # 26-27', '1983-09-09'),
    ('101010101', 'Juan', 'Morales', '3091010101', 'Calle 28 # 29-30', '1980-10-10'),
    -- Doctores Periodoncia
    ('111111112', 'Karina', 'Rojas', '3101111112', 'Carrera 31 # 32-33', '1981-11-11'),
    ('222222223', 'Luis', 'Vargas', '3112222223', 'Avenida 34 # 35-36', '1982-12-12'),
    -- Doctores Odontología Estética
    ('333333334', 'María', 'Silva', '3123333334', 'Calle 37 # 38-39', '1984-01-13'),
    ('444444445', 'Nicolás', 'Cruz', '3134444445', 'Carrera 40 # 41-42', '1983-02-14'),
    -- Pacientes
    ('555555556', 'Ana', 'Martínez', '3145555556', 'Calle 43 # 44-45', '1990-03-15'),
    ('666666667', 'Bruno', 'González', '3156666667', 'Carrera 46 # 47-48', '1992-04-16'),
    ('777777778', 'Carmen', 'Rodríguez', '3167777778', 'Avenida 49 # 50-51', '1988-05-17'),
    ('888888889', 'Diego', 'Hernández', '3178888889', 'Calle 52 # 53-54', '1995-06-18'),
    ('999999990', 'Elena', 'Díaz', '3189999990', 'Carrera 55 # 56-57', '1991-07-19'),
    ('101010102', 'Felipe', 'Moreno', '3191010102', 'Avenida 58 # 59-60', '1993-08-20'),
    ('111111113', 'Gloria', 'Jiménez', '3201111113', 'Calle 61 # 62-63', '1989-09-21'),
    ('222222224', 'Hugo', 'Paredes', '3212222224', 'Carrera 64 # 65-66', '1994-10-22'),
    ('333333335', 'Iris', 'Quintero', '3223333335', 'Avenida 67 # 68-69', '1992-11-23'),
    ('444444446', 'Jorge', 'Valencia', '3234444446', 'Calle 70 # 71-72', '1990-12-24');

-- Insertar cuentas para doctores
INSERT INTO cuentas (email, password, rol, status, user_id, created_at)
VALUES
    ('andres.torres@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '111111111', NOW()),
    ('beatriz.martinez@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '222222222', NOW()),
    ('carlos.garcia@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '333333333', NOW()),
    ('diana.lopez@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '444444444', NOW()),
    ('eduardo.ramirez@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '555555555', NOW()),
    ('fernanda.sanchez@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '666666666', NOW()),
    ('gabriel.perez@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '777777777', NOW()),
    ('hector.gomez@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '888888888', NOW()),
    ('isabel.castro@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '999999999', NOW()),
    ('juan.morales@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '101010101', NOW()),
    ('karina.rojas@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '111111112', NOW()),
    ('luis.vargas@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '222222223', NOW()),
    ('maria.silva@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '333333334', NOW()),
    ('nicolas.cruz@clinica.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'DOCTOR', 'ACTIVE', '444444445', NOW());

-- Insertar cuentas para pacientes
INSERT INTO cuentas (email, password, rol, status, user_id, created_at)
VALUES
    ('ana.martinez@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '555555556', NOW()),
    ('bruno.gonzalez@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '666666667', NOW()),
    ('carmen.rodriguez@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '777777778', NOW()),
    ('diego.hernandez@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '888888889', NOW()),
    ('elena.diaz@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '999999990', NOW()),
    ('felipe.moreno@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '101010102', NOW()),
    ('gloria.jimenez@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '111111113', NOW()),
    ('hugo.paredes@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '222222224', NOW()),
    ('iris.quintero@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '333333335', NOW()),
    ('jorge.valencia@email.com', '$2a$10$EGsmTjDhUXCfSMLbYgSGsOt3ejDVZo9z0/1QDHGXSVmWpA8.Mcqg2', 'PACIENTE', 'ACTIVE', '444444446', NOW());

-- Asignar especialidades a doctores
INSERT INTO doctor_especialidad (doctor_id, especialidad_id) VALUES
                                                                 -- Odontología General
                                                                 ('111111111', 1),
                                                                 ('222222222', 1),
                                                                 -- Higiene Dental
                                                                 ('333333333', 2),
                                                                 ('444444444', 2),
                                                                 -- Cirugía Oral
                                                                 ('555555555', 3),
                                                                 ('666666666', 3),
                                                                 -- Endodoncia
                                                                 ('777777777', 4),
                                                                 ('888888888', 4),
                                                                 -- Ortodoncia
                                                                 ('999999999', 5),
                                                                 ('101010101', 5),
                                                                 -- Periodoncia
                                                                 ('111111112', 6),
                                                                 ('222222223', 6),
                                                                 -- Odontología Estética
                                                                 ('333333334', 7),
                                                                 ('444444445', 7);

-- Insertar disponibilidad de doctores (todos los días de lunes a viernes)
INSERT INTO disponibilidad_doctor (doctor_id, dia_semana, hora_inicio, hora_fin, estado, intervalo_citas, fecha_creacion) VALUES
                                                                                                                              -- Odontología General
                                                                                                                              ('111111111', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 30, NOW()),
                                                                                                                              ('111111111', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 30, NOW()),
                                                                                                                              ('222222222', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 30, NOW()),
                                                                                                                              ('222222222', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 30, NOW()),
                                                                                                                              -- Higiene Dental
                                                                                                                              ('333333333', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              ('333333333', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              ('444444444', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              ('444444444', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              -- Cirugía Oral
                                                                                                                              ('555555555', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('555555555', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('666666666', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('666666666', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              -- Endodoncia
                                                                                                                              ('777777777', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 90, NOW()),
                                                                                                                              ('777777777', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 90, NOW()),
                                                                                                                              ('888888888', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 90, NOW()),
                                                                                                                              ('888888888', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 90, NOW()),
                                                                                                                              -- Ortodoncia
                                                                                                                              ('999999999', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              ('999999999', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              ('101010101', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              ('101010101', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 45, NOW()),
                                                                                                                              -- Periodoncia
                                                                                                                              ('111111112', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('111111112', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('222222223', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('222222223', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              -- Odontología Estética
                                                                                                                              ('333333334', 'MONDAY', '08:00:00', '17:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('333333334', 'WEDNESDAY', '08:00:00', '17:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('444444445', 'TUESDAY', '09:00:00', '18:00:00', 'ACTIVO', 60, NOW()),
                                                                                                                              ('444444445', 'THURSDAY', '09:00:00', '18:00:00', 'ACTIVO', 60, NOW());

-- Insertar citas para 2025
INSERT INTO citas (paciente_id, doctor_id, fecha_hora, estado, tipo_cita_id, es_autenticada, 
                  nombre_paciente_no_autenticado, numero_identificacion_no_autenticado, 
                  telefono_no_autenticado, email_no_autenticado) VALUES
    -- Citas con Odontología General (Lunes y Miércoles 8:00-17:00)
    ('555555556', '111111111', '2025-01-06 09:00:00', 'PENDIENTE', 1, true, NULL, NULL, NULL, NULL),
    ('666666667', '222222222', '2025-01-07 10:00:00', 'CONFIRMADA', 1, true, NULL, NULL, NULL, NULL),
    -- Citas con Higiene Dental (Lunes y Miércoles 8:00-17:00)
    ('777777778', '333333333', '2025-01-06 11:00:00', 'PENDIENTE', 2, true, NULL, NULL, NULL, NULL),
    ('888888889', '444444444', '2025-01-07 14:00:00', 'CONFIRMADA', 2, true, NULL, NULL, NULL, NULL),
    -- Citas con Cirugía Oral (Lunes y Miércoles 8:00-17:00)
    ('999999990', '555555555', '2025-01-08 09:00:00', 'PENDIENTE', 3, true, NULL, NULL, NULL, NULL),
    ('101010102', '666666666', '2025-01-09 10:00:00', 'CONFIRMADA', 3, true, NULL, NULL, NULL, NULL),
    -- Citas con Endodoncia (Lunes y Miércoles 8:00-17:00)
    ('111111113', '777777777', '2025-01-06 13:00:00', 'PENDIENTE', 4, true, NULL, NULL, NULL, NULL),
    ('222222224', '888888888', '2025-01-07 15:00:00', 'CONFIRMADA', 4, true, NULL, NULL, NULL, NULL),
    -- Citas con Ortodoncia (Lunes y Miércoles 8:00-17:00)
    ('333333335', '999999999', '2025-01-08 11:00:00', 'PENDIENTE', 5, true, NULL, NULL, NULL, NULL),
    ('444444446', '101010101', '2025-01-09 13:00:00', 'CONFIRMADA', 5, true, NULL, NULL, NULL, NULL),
    -- Citas con Periodoncia (Lunes y Miércoles 8:00-17:00)
    ('555555556', '111111112', '2025-01-06 15:00:00', 'PENDIENTE', 6, true, NULL, NULL, NULL, NULL),
    ('666666667', '222222223', '2025-01-07 16:00:00', 'CONFIRMADA', 6, true, NULL, NULL, NULL, NULL),
    -- Citas con Odontología Estética (Lunes y Miércoles 8:00-17:00)
    ('777777778', '333333334', '2025-01-08 14:00:00', 'PENDIENTE', 7, true, NULL, NULL, NULL, NULL),
    ('888888889', '444444445', '2025-01-09 15:00:00', 'CONFIRMADA', 7, true, NULL, NULL, NULL, NULL),
    -- Más citas distribuidas
    ('999999990', '111111111', '2025-01-13 09:00:00', 'PENDIENTE', 1, true, NULL, NULL, NULL, NULL),
    ('101010102', '333333333', '2025-01-13 10:00:00', 'CONFIRMADA', 2, true, NULL, NULL, NULL, NULL),
    ('111111113', '555555555', '2025-01-13 11:00:00', 'PENDIENTE', 3, true, NULL, NULL, NULL, NULL),
    ('222222224', '777777777', '2025-01-13 13:00:00', 'CONFIRMADA', 4, true, NULL, NULL, NULL, NULL),
    ('333333335', '999999999', '2025-01-13 14:00:00', 'PENDIENTE', 5, true, NULL, NULL, NULL, NULL),
    ('444444446', '111111112', '2025-01-13 15:00:00', 'CONFIRMADA', 6, true, NULL, NULL, NULL, NULL),
    ('555555556', '333333334', '2025-01-13 16:00:00', 'PENDIENTE', 7, true, NULL, NULL, NULL, NULL);

-- Insertar registros de historial médico asociados a las citas existentes
INSERT INTO historiales_medicos (fecha, proxima_cita, cita_id, doctor_id, paciente_id, diagnostico, observaciones, tratamiento) VALUES
                                                                                                                                    -- Historiales para Odontología General
                                                                                                                                    ('2024-04-01', '2024-07-01', 1, '111111111', '555555556', 'Caries moderada en primer molar inferior derecho', 'Paciente con alta sensibilidad al frío', 'Obturación con resina compuesta y aplicación de flúor'),
                                                                                                                                    ('2024-04-02', '2024-07-02', 2, '222222222', '666666667', 'Gingivitis leve', 'Sangrado al cepillado', 'Limpieza profesional y técnica de cepillado mejorada'),

                                                                                                                                    -- Historiales para Higiene Dental
                                                                                                                                    ('2024-04-01', '2024-06-01', 3, '333333333', '777777778', 'Acumulación de sarro moderado', 'Manchas de café visibles', 'Profilaxis completa y pulido dental'),
                                                                                                                                    ('2024-04-02', '2024-06-02', 4, '444444444', '888888889', 'Halitosis', 'Lengua saburral', 'Limpieza profunda y recomendación de raspador lingual'),

                                                                                                                                    -- Historiales para Cirugía Oral
                                                                                                                                    ('2024-04-03', '2024-04-10', 5, '555555555', '999999990', 'Tercer molar inferior impactado', 'Inflamación pericoronaria', 'Extracción quirúrgica con sutura reabsorbible'),
                                                                                                                                    ('2024-04-04', '2024-04-11', 6, '666666666', '101010102', 'Raíz residual en zona 37', 'Tejido gingival sano', 'Apicectomía y curetaje'),

                                                                                                                                    -- Historiales para Endodoncia
                                                                                                                                    ('2024-04-01', '2024-05-01', 7, '777777777', '111111113', 'Necrosis pulpar en diente 36', 'Fístula vestibular', 'Tratamiento de conductos en tres sesiones'),
                                                                                                                                    ('2024-04-02', '2024-05-02', 8, '888888888', '222222224', 'Fractura coronaria con exposición pulpar', 'Dolor espontáneo', 'Pulpectomía completa y reconstrucción'),

                                                                                                                                    -- Historiales para Ortodoncia
                                                                                                                                    ('2024-04-03', '2024-05-15', 9, '999999999', '333333335', 'Maloclusión clase II división 1', 'Hábito de succión digital', 'Colocación de brackets metálicos superiores'),
                                                                                                                                    ('2024-04-04', '2024-05-18', 10, '101010101', '444444446', 'Apiñamiento moderado inferior', 'Encías retraídas en zona anterior', 'Alineadores transparentes'),

                                                                                                                                    -- Historiales para Periodoncia
                                                                                                                                    ('2024-04-01', '2024-05-01', 11, '111111112', '555555556', 'Bolsa periodontal de 5mm en sector posterior', 'Movilidad grado I', 'Raspado y alisado radicular'),
                                                                                                                                    ('2024-04-02', '2024-05-02', 12, '222222223', '666666667', 'Recesión gingival en caninos', 'Sensibilidad radicular', 'Injerto de tejido conectivo'),

                                                                                                                                    -- Historiales para Odontología Estética
                                                                                                                                    ('2024-04-03', NULL, 13, '333333334', '777777778', 'Diastema interincisivo superior', 'Paciente desea cierre estético', 'Carillas de porcelana'),
                                                                                                                                    ('2024-04-04', '2024-05-04', 14, '444444445', '888888889', 'Fluorosis moderada', 'Manchas blancas difusas', 'Microabrasión y blanqueamiento profesional'),

                                                                                                                                    -- Historiales adicionales
                                                                                                                                    ('2024-04-05', '2024-07-05', 15, '111111111', '999999990', 'Caries oclusal superficial', 'Paciente con buena higiene', 'Sellante de fosas y fisuras'),
                                                                                                                                    ('2024-04-05', '2024-06-05', 16, '333333333', '101010102', 'Hipoplasia del esmalte', 'Defectos en incisivos', 'Restauración con ionómero de vidrio'),
                                                                                                                                    ('2024-04-05', '2024-04-12', 17, '555555555', '111111113', 'Quiste dentígero asociado a canino', 'Asintomático', 'Enucleación quirúrgica'),
                                                                                                                                    ('2024-04-05', '2024-05-20', 18, '777777777', '222222224', 'Reabsorción radicular interna', 'Traumatismo previo', 'Control radiográfico cada 6 meses'),
                                                                                                                                    ('2024-04-05', '2024-05-25', 19, '999999999', '333333335', 'Mordida cruzada posterior', 'Asimetría facial leve', 'Expansor palatino'),
                                                                                                                                    ('2024-04-05', '2024-05-05', 20, '111111112', '444444446', 'Furca grado II en molar inferior', 'Pérdida ósea vertical', 'Regeneración tisular guiada'),
                                                                                                                                    ('2024-04-05', NULL, 21, '333333334', '555555556', 'Abrasión cervical generalizada', 'Cepillado traumático', 'Restauraciones con resina fluida');

-- Reactivar restricciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;