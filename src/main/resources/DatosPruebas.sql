-- 1️⃣ Desactivar temporalmente restricciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- 2️⃣ Eliminar primero las cuentas (depende de `usuarios_detalles`)
DELETE
FROM cuentas;

-- 3️⃣ Luego eliminar los usuarios
DELETE
FROM usuarios_detalles;

DELETE
FROM citas;

-- 4️⃣ Reactivar restricciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- 5️⃣ Insertar usuarios (incluye DOCTORES y PACIENTES)
INSERT INTO usuarios_detalles (id_number, name, last_name, phone_number, address, birth_date)
VALUES
    -- ODONTOLOGOS GENERALES
    ('111111111', 'Andrés', 'Torres', '3001111111', 'Calle 1 # 2-3', '1980-01-01'),
    ('222222222', 'Beatriz', 'Martínez', '3012222222', 'Carrera 4 # 5-6', '1982-02-02'),

    -- HIGIENISTAS DENTALES
    ('333333333', 'Camilo', 'Fernández', '3023333333', 'Avenida 7 # 8-9', '1984-03-03'),
    ('444444444', 'Daniela', 'Paredes', '3034444444', 'Calle 10 # 11-12', '1986-04-04'),

    -- CIRUJANOS ORALES Y MAXILOFACIALES
    ('555555555', 'Eduardo', 'Ríos', '3045555555', 'Carrera 13 # 14-15', '1988-05-05'),
    ('666666666', 'Fernanda', 'Quintero', '3056666666', 'Calle 16 # 17-18', '1990-06-06'),

    -- ENDODONCISTAS
    ('123456789', 'Juan', 'Pérez', '3201234567', 'Calle 45 # 23-10', '1980-05-15'),
    ('777777777', 'Gabriela', 'López', '3067777777', 'Carrera 19 # 20-21', '1992-07-07'),

    -- ORTODONCISTAS
    ('987654321', 'María', 'Gómez', '3107654321', 'Carrera 12 # 56-78', '1985-09-20'),
    ('888888888', 'Hugo', 'Ramírez', '3078888888', 'Avenida 22 # 23-24', '1994-08-08'),

    -- PERIODONCISTAS
    ('999999999', 'Isabel', 'Salazar', '3089999999', 'Calle 25 # 26-27', '1996-09-09'),
    ('101010101', 'Javier', 'Ortega', '3091010101', 'Carrera 28 # 29-30', '1998-10-10'),

    -- ODONTÓLOGOS ESTÉTICOS
    ('121212121', 'Karen', 'González', '3101212121', 'Calle 31 # 32-33', '2000-11-11'),
    ('131313131', 'Luis', 'Castillo', '3111313131', 'Avenida 34 # 35-36', '2002-12-12'),

    -- PACIENTES
    ('111000111', 'Sofía', 'Mejía', '3201112222', 'Calle 50 # 12-34', '1990-01-15'),
    ('222000222', 'Diego', 'Velásquez', '3212223333', 'Carrera 7 # 89-10', '1985-04-22'),
    ('333000333', 'Valentina', 'Ríos', '3223334444', 'Avenida 15 # 78-45', '1992-07-30'),
    ('444000444', 'Mateo', 'Cardona', '3234445555', 'Calle 60 # 20-30', '1988-10-10'),
    ('555000555', 'Camila', 'Gutiérrez', '3245556666', 'Carrera 22 # 11-12', '1995-05-05'),
    ('666000666', 'Julián', 'Montoya', '3256667777', 'Avenida 9 # 66-77', '1987-11-25'),
    ('777000777', 'Fernanda', 'Londoño', '3267778888', 'Calle 100 # 5-55', '1993-03-08'),
    ('888000888', 'Andrés', 'Muñoz', '3278889999', 'Carrera 14 # 8-22', '1990-08-18'),
    ('999000999', 'Laura', 'Gaviria', '3289990000', 'Calle 80 # 45-67', '1996-09-02'),
    ('101000101', 'Carlos', 'Zapata', '3291010101', 'Avenida 40 # 77-99', '1989-12-12');


INSERT INTO cuentas (email, password, rol, status, user_id, tipo_doctor, created_at)
VALUES
    -- ODONTOLOGOS GENERALES
    ('andres.torres@clinica.com', '$2a$10$XT7QRuo1TuqmcGihg5CN1uFAhL5nat3bqamFTGAgefp3VwP/DMqVe', 'DOCTOR', 'ACTIVE', '111111111', 'ODONTOLOGO_GENERAL', NOW()),
    ('beatriz.martinez@clinica.com', '$2a$10$hZDofY1Sc.fBvGnE8X2Q4eSFI5JBML/iB54r0C0V.Yz7U0JIALIaq', 'DOCTOR', 'ACTIVE', '222222222', 'ODONTOLOGO_GENERAL', NOW()),

    -- HIGIENISTAS DENTALES
    ('camilo.fernandez@clinica.com', '$2a$10$SUq/4RlcDDmYtfxwWadCtOfrpZ31lXhOhlINr5SiBI4EtfW9y/CJ6', 'DOCTOR', 'ACTIVE', '333333333', 'HIGIENISTA_DENTAL', NOW()),
    ('daniela.paredes@clinica.com', '$2a$10$yY5g1sLXRa6HexmHa0AS7.ndIrL3i3CRdeLFv/Y7cwPDWw.hZa.c6', 'DOCTOR', 'ACTIVE', '444444444', 'HIGIENISTA_DENTAL', NOW()),

    -- CIRUJANOS ORALES Y MAXILOFACIALES
    ('eduardo.rios@clinica.com', '$2a$10$w.LOsuR2D.Gi4woi33EUve9Xp0JsdyJiOrQjqHpPoc5pP/91XsPZW', 'DOCTOR', 'ACTIVE', '555555555', 'CIRUJANO_ORAL_Y_MAXILOFACIAL', NOW()),
    ('fernanda.quintero@clinica.com', '$2a$10$dpKT0RAzLsBjcOVG79DlQuSejRsxhcRZ2uQTSca.LzM/PWXg0YLYK', 'DOCTOR', 'ACTIVE', '666666666', 'CIRUJANO_ORAL_Y_MAXILOFACIAL', NOW()),

    -- ENDODONCISTAS
    ('juan.perez@clinica.com', '$2a$10$4X88HzfWxkzwyuZKUsn/k.LASJfXmBrEYkMVAjSDar9dOdQY/eCGK', 'DOCTOR', 'ACTIVE', '123456789', 'ENDODONCISTA', NOW()),
    ('gabriela.lopez@clinica.com', '$2a$10$3F31BSxtjEGRk6ZEAFzxNeHLPJxh8HQht2uW1EDitxNWZi4bIgo62', 'DOCTOR', 'ACTIVE', '777777777', 'ENDODONCISTA', NOW()),

    -- ORTODONCISTAS
    ('maria.gomez@clinica.com', '$2a$10$1gir6D.2hOtX9l4T3seK/u4wJ7H/kdxl3i9h4mmLlgUh510GNh5My', 'DOCTOR', 'ACTIVE', '987654321', 'ORTODONCISTA', NOW()),
    ('hugo.ramirez@clinica.com', '$2a$10$HxD4WVrXIJWMnz11al/aBOdeDHQkx8x8wKnRb.HIdZAVYfFM4qZrq', 'DOCTOR', 'ACTIVE', '888888888', 'ORTODONCISTA', NOW()),

    -- PERIODONCISTAS
    ('isabel.salazar@clinica.com', '$2a$10$/5PDcnCIYtvQs7wQIeZ4pOZpzFJC3YPOMpUCmw5xmvCJOiI/6BXlC', 'DOCTOR', 'ACTIVE', '999999999', 'PERIODONCISTA', NOW()),
    ('javier.ortega@clinica.com', '$2a$10$43VQmQHBwyxmsp8QhqhDQ.xG.unhxMR3NVzDGBds.7JdfyU0W.Pgi', 'DOCTOR', 'ACTIVE', '101010101', 'PERIODONCISTA', NOW()),

    -- ODONTÓLOGOS ESTÉTICOS
    ('karen.gonzalez@clinica.com', '$2a$10$cRF5R8.fXojqWOlBqKoJUOwUlB1ouB7pucZCtBqNcdATMlR0ge9qq', 'DOCTOR', 'ACTIVE', '121212121', 'ODONTOLOGO_ESTETICO', NOW()),
    ('luis.castillo@clinica.com', '$2a$10$3zlrc5FHTqsGupFMtBHV6uMgH8LUU/MysFn2a5utgzxzUKOX6fLQS', 'DOCTOR', 'ACTIVE', '131313131', 'ODONTOLOGO_ESTETICO', NOW()),

    -- PACIENTES
    ('sofia.mejia@clinica.com', '$2a$10$TncYEQsmq7F/R2LzSH1lVeExL.CEfd2Y6H5Ce/7C/ZF7vH7MfMcqm', 'PACIENTE', 'ACTIVE', '111000111', NULL, NOW()),
    ('diego.velasquez@clinica.com', '$2a$10$gvPyqlOS2wSNtfxYuVTiZeLqDqzPReAKd3apNRkcyHCUl.QXKH1Om', 'PACIENTE', 'ACTIVE', '222000222', NULL, NOW()),
    ('valentina.rios@clinica.com', '$2a$10$LIBG4y0kbUi6b2UOpo4lOOp6RVGol/LfpUQF9kIb8EkiVJfiqLr1e', 'PACIENTE', 'ACTIVE', '333000333', NULL, NOW()),
    ('mateo.cardona@clinica.com', '$2a$10$dwauCMpJm7h7.bq0QqN/2.AyJ/2KPDQ3PG2rRV9WsZzVs9acD9OLu', 'PACIENTE', 'ACTIVE', '444000444', NULL, NOW()),
    ('camila.gutierrez@clinica.com', '$2a$10$Yiv7G45lunp/1w/JOH1dku.7CcJFNbUWPgLdFc4SDdBUiWfWDFsoa', 'PACIENTE', 'ACTIVE', '555000555', NULL, NOW()),
    ('julian.montoya@clinica.com', '$2a$10$Qd1MmFBTBObdBVYQxRJNFegrNijK8qf0NigSgMMHTj9gCx.ht2z7i', 'PACIENTE', 'ACTIVE', '666000666', NULL, NOW()),
    ('fernanda.londono@clinica.com', '$2a$10$1vyLRpLY.sMWSGf7dlgZ1u/lnhXiFtgHpE37eA8RUNF1lB2lo0lOS', 'PACIENTE', 'ACTIVE', '777000777', NULL, NOW()),
    ('andres.munoz@clinica.com', '$2a$10$EOAlaUFqalG1urBbXqyj5.vJhgwue6u9YucrsigwonkpmsSPAweai', 'PACIENTE', 'ACTIVE', '888000888', NULL, NOW()),
    ('laura.gaviria@clinica.com', '$2a$10$wyvsBfJWU7iQR2vy7uYPGeBPIY83G2hOnLlm2A7YbvKGItqfv2zoO', 'PACIENTE', 'ACTIVE', '999000999', NULL, NOW()),
    ('carlos.zapata@clinica.com', '$2a$10$wfpuTYSfzaM3R/w6CZ09mughqySbb7G3DlNiEEShiiGz8i6lWJXaa', 'PACIENTE', 'ACTIVE', '101000101', NULL, NOW());

-- 6️⃣ Insertar citas
INSERT INTO citas (paciente_id, odontologo_id, fecha_hora, estado, tipo_cita) VALUES
    -- Citas con Odontólogos Generales
    ('111000111', '111111111', '2024-04-01 09:00:00', 'PENDIENTE', 'CONSULTA_GENERAL'),
    ('222000222', '222222222', '2024-04-02 10:30:00', 'CONFIRMADA', 'CONSULTA_GENERAL'),

    -- Citas con Higienistas Dentales
    ('333000333', '333333333', '2024-04-03 11:00:00', 'PENDIENTE', 'LIMPIEZA_DENTAL'),
    ('444000444', '444444444', '2024-04-04 14:00:00', 'CONFIRMADA', 'LIMPIEZA_DENTAL'),

    -- Citas con Cirujanos Orales
    ('555000555', '555555555', '2024-04-05 15:30:00', 'PENDIENTE', 'EXTRACCION_DIENTES'),
    ('666000666', '666666666', '2024-04-06 16:00:00', 'CONFIRMADA', 'EXTRACCION_DIENTES'),

    -- Citas con Endodoncistas
    ('777000777', '123456789', '2024-04-07 09:30:00', 'PENDIENTE', 'TRATAMIENTO_DE_CONDUCTO'),
    ('888000888', '777777777', '2024-04-08 10:00:00', 'CONFIRMADA', 'TRATAMIENTO_DE_CONDUCTO'),

    -- Citas con Ortodoncistas
    ('999000999', '987654321', '2024-04-09 11:30:00', 'PENDIENTE', 'ORTODONCIA'),
    ('101000101', '888888888', '2024-04-10 14:30:00', 'CONFIRMADA', 'ORTODONCIA'),

    -- Citas con Periodoncistas
    ('111000111', '999999999', '2024-04-11 15:00:00', 'PENDIENTE', 'IMPLANTES_DENTALES'),
    ('222000222', '101010101', '2024-04-12 16:30:00', 'CONFIRMADA', 'IMPLANTES_DENTALES'),

    -- Citas con Odontólogos Estéticos
    ('333000333', '121212121', '2024-04-13 09:00:00', 'PENDIENTE', 'BLANQUEAMIENTO_DENTAL'),
    ('444000444', '131313131', '2024-04-14 10:30:00', 'CONFIRMADA', 'BLANQUEAMIENTO_DENTAL'),

    -- Segunda ronda de citas para algunos pacientes
    ('555000555', '111111111', '2024-04-15 11:00:00', 'PENDIENTE', 'CONSULTA_GENERAL'),
    ('666000666', '333333333', '2024-04-16 14:00:00', 'CONFIRMADA', 'LIMPIEZA_DENTAL'),
    ('777000777', '555555555', '2024-04-17 15:30:00', 'PENDIENTE', 'EXTRACCION_DIENTES'),
    ('888000888', '123456789', '2024-04-18 16:00:00', 'CONFIRMADA', 'TRATAMIENTO_DE_CONDUCTO'),
    ('999000999', '987654321', '2024-04-19 09:30:00', 'PENDIENTE', 'ORTODONCIA'),
    ('101000101', '999999999', '2024-04-20 10:00:00', 'CONFIRMADA', 'IMPLANTES_DENTALES');

-- Contraseña de los doctores
/*
passwords = [
    "hashed_password1", "hashed_password2", "hashed_password3",
    "hashed_password4", "hashed_password5", "hashed_password6",
    "hashed_password7", "hashed_password8", "hashed_password9",
    "hashed_password10", "hashed_password11", "hashed_password12",
    "hashed_password13", "hashed_password14"
]
*/

-- Contraseña de los pacientes
/* // Lista de contraseñas en texto plano
        String[] rawPasswords = {
                "contraseña1", "contraseña2", "contraseña3",
                "contraseña4", "contraseña5", "contraseña6",
                "contraseña7", "contraseña8", "contraseña9",
                "contraseña10"
        };
 */