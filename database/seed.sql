-- =============================================================================
-- SIBECA-18: Sistema Web de Seguimiento del Proceso Beca 18 - Convocatoria 2027
-- Archivo: database/seed.sql
-- Motor: PostgreSQL 15 / 16 / 17 (Compatible con pgAdmin 4 Query Tool)
-- Descripción: Datos iniciales obligatorios según Examen_Practico_03.pdf (Sección 10)
--   - 1 Usuario Administrador (usuario: admin / clave: admin123)
--   - 9 Modalidades oficiales de Beca 18
--   - 8 Etapas parametrizables del cronograma 2027
--   - 5 Instituciones y 5 Programas de estudios (incluyendo 1 con una sola vacante)
--   - 12 Postulantes ficticios exactos (casos 1 al 12 de la Tabla 10)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Parámetros del Sistema (Fecha de corte parametrizable RN-02: 09/10/2026)
-- -----------------------------------------------------------------------------
INSERT INTO parametro_sistema (clave, valor, descripcion) VALUES
('FECHA_CORTE_EDAD', '2026-10-09', 'Fecha de corte parametrizable para el cálculo de edad (RN-02: publicación de bases)'),
('CONVOCATORIA_ANIO', '2027', 'Año de la convocatoria vigente de Beca 18'),
('PUNTAJE_MAXIMO_ENP', '120', 'Puntaje máximo referencial del Examen Nacional de Preselección (RN-09)');

-- -----------------------------------------------------------------------------
-- 2. Usuarios (Contraseñas almacenadas con hash SHA-256 en hexadecimal)
--    Administrador -> usuario: admin               | clave: admin123
--                     SHA-256('admin123') = 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--    Postulantes   -> usuario: 70100001..70100012  | clave: beca2027
--                     SHA-256('beca2027') = 37f6ca0f02960e3927848c0a8e943a77600810f71fa042ff94158b1f2fa1d3d8
-- -----------------------------------------------------------------------------
INSERT INTO usuario (id_usuario, username, password_hash, rol, activo) VALUES
(1,  'admin',    encode(sha256('admin123'::bytea), 'hex'), 'ADMINISTRADOR', 1),
(2,  '70100001', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(3,  '70100002', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(4,  '70100003', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(5,  '70100004', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(6,  '70100005', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(7,  '70100006', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(8,  '70100007', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(9,  '70100008', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(10, '70100009', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(11, '70100010', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(12, '70100011', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1),
(13, '70100012', encode(sha256('beca2027'::bytea), 'hex'), 'POSTULANTE',    1);

-- -----------------------------------------------------------------------------
-- 3. Nueve Modalidades de Postulación (Sección 5.2, RN-02, RN-09)
-- -----------------------------------------------------------------------------
INSERT INTO modalidad (id_modalidad, codigo, nombre, tipo, edad_maxima, puntaje_minimo_enp, descripcion) VALUES
(1, 'ORD',     'Ordinaria',                                'ORDINARIA', 22,   84, 'Modalidad regular para jóvenes con alto rendimiento académico en condición de pobreza o pobreza extrema.'),
(2, 'HUA',     'Huallaga',                                 'ESPECIAL',  22,   66, 'Modalidad especial dirigida a residentes de la zona del Huallaga.'),
(3, 'VRAEM',   'Vraem',                                    'ESPECIAL',  22,   66, 'Modalidad especial dirigida a residentes del Valle de los ríos Apurímac, Ene y Mantaro.'),
(4, 'PROT',    'Protección',                               'ESPECIAL',  22,   66, 'Modalidad especial para adolescentes con protección estatal o desprotección familiar.'),
(5, 'REPARED', 'Repared',                                  'ESPECIAL',  NULL, 66, 'Modalidad especial para víctimas de la violencia habida en el país entre 1980 y 2000 (sin límite de edad).'),
(6, 'CNA',     'Comunidades Nativas Amazónicas (CNA)',     'ESPECIAL',  NULL, 66, 'Modalidad especial para integrantes de comunidades nativas amazónicas (sin límite de edad).'),
(7, 'PA',      'Pueblo Afroperuano (PA)',                  'ESPECIAL',  NULL, 66, 'Modalidad especial para integrantes del pueblo afroperuano (sin límite de edad).'),
(8, 'EIB',     'Educación Intercultural Bilingüe (EIB)',   'ESPECIAL',  NULL, 66, 'Modalidad especial para formación en Educación Intercultural Bilingüe (sin límite de edad).'),
(9, 'FFAA',    'Fuerzas Armadas',                          'ESPECIAL',  31,   66, 'Modalidad especial para licenciados del Servicio Militar Voluntario (menor de 31 años).');

-- -----------------------------------------------------------------------------
-- 4. Ocho Etapas del Cronograma 2027 (Sección 5.1, parametrizables por Admin)
-- -----------------------------------------------------------------------------
INSERT INTO etapa (id_etapa, orden, codigo, nombre, fecha_referencia, fecha_inicio, fecha_fin, publicada, descripcion) VALUES
(1, 1, 'INSCRIPCION_ENP',     'Inscripción al Examen Nacional de Preselección (ENP)', '09/10/2026 al 18/10/2026', '2026-10-09', '2026-12-31', 1, 'Registro público de postulantes y edición de ficha de inscripción.'),
(2, 2, 'REVISION_INSCRIPCION','Revisión de la inscripción',                            '10/10/2026 al 06/11/2026', '2026-10-10', '2026-12-31', 1, 'Validación de requisitos documentales y socioeconómicos por el administrador.'),
(3, 3, 'PUBLICACION_APTOS',   'Publicación de postulantes aptos para rendir el ENP',  '11/11/2026',               '2026-11-11', '2026-11-11', 1, 'Publicación oficial de resultados de revisión (APTO / NO APTO).'),
(4, 4, 'APLICACION_ENP',      'Aplicación del ENP',                                   '22/11/2026',               '2026-11-22', '2026-11-22', 1, 'Evaluación presencial de 60 preguntas a nivel nacional.'),
(5, 5, 'PUBLICACION_PRESEL',  'Publicación de la lista de preseleccionados',          '23/12/2026',               '2026-12-23', '2026-12-23', 1, 'Publicación de puntajes del ENP y condición de preseleccionado.'),
(6, 6, 'POSTULACION_MOM1',    'Postulación para la selección, primer momento',        '07/01/2027 al 25/02/2027', '2026-11-01', '2027-02-25', 1, 'Elección de institución y programa de estudios elegible (Momento 1).'),
(7, 7, 'POSTULACION_MOM2',    'Postulación para la selección, segundo momento',       '07/04/2027 al 30/04/2027', '2027-04-07', '2027-04-30', 0, 'Elección de institución y programa de estudios elegible (Momento 2).'),
(8, 8, 'PUBLICACION_FINAL',   'Publicación final de seleccionados',                   'Definida por administrador','2027-05-05', '2027-05-15', 0, 'Publicación oficial de ganadores de Beca 18 según ranking de mérito.');

-- -----------------------------------------------------------------------------
-- 5. Instituciones y Programas de Estudios (Mínimo 4 programas, uno con 1 sola vacante)
-- -----------------------------------------------------------------------------
INSERT INTO institucion (id_institucion, nombre, tipo_gestion, region, elegible) VALUES
(1, 'Universidad Nacional de Ingeniería (UNI)',              'PUBLICA', 'Lima',     1),
(2, 'Pontificia Universidad Católica del Perú (PUCP)',       'PRIVADA', 'Lima',     1),
(3, 'Universidad Nacional Mayor de San Marcos (UNMSM)',      'PUBLICA', 'Lima',     1),
(4, 'Universidad Nacional de San Cristóbal de Huamanga',     'PUBLICA', 'Ayacucho', 1),
(5, 'Escuela Superior La Pontificia (ELP)',                  'PRIVADA', 'Ayacucho', 1);

INSERT INTO programa_estudio (id_programa, id_institucion, institucion, programa_estudios, tipo_gestion, region, vacantes, activo) VALUES
(1, 1, 'Universidad Nacional de Ingeniería (UNI)',          'Ingeniería de Sistemas',               'PUBLICA', 'Lima',     1, 1),
(2, 2, 'Pontificia Universidad Católica del Perú (PUCP)',   'Ingeniería Informática',               'PRIVADA', 'Lima',     2, 1),
(3, 3, 'Universidad Nacional Mayor de San Marcos (UNMSM)',  'Medicina Humana',                      'PUBLICA', 'Lima',     2, 1),
(4, 4, 'Universidad Nacional de San Cristóbal de Huamanga', 'Ingeniería Civil',                     'PUBLICA', 'Ayacucho', 3, 1),
(5, 5, 'Escuela Superior La Pontificia (ELP)',              'Ingeniería de Sistemas de Información','PRIVADA', 'Ayacucho', 3, 1);

-- -----------------------------------------------------------------------------
-- 6. Doce Postulantes Ficticios Obligatorios (Sección 10 del Examen)
--    Fecha de corte: 2026-10-09
-- -----------------------------------------------------------------------------
INSERT INTO postulante (
    id_postulante, id_usuario, dni, nombres, apellidos, fecha_nacimiento, edad_corte,
    nacionalidad, correo, celular, region, colegio, situacion_estudios, anio_egreso,
    tercio_superior, clasificacion_socioeconomica, discapacidad_acreditada,
    id_modalidad, estado, observacion_revision, puntaje_enp, fecha_inscripcion
) VALUES
-- N.° 1: Ordinaria, 18 años, puntaje 90 (Preselección claramente aprobada)
(1, 2, '70100001', 'Valeria Sofía', 'Quispe Mendoza', '2008-04-15', 18,
 'PERUANA', 'valeria.quispe@correo.pe', '987100001', 'Ayacucho', 'I.E. Mariscal Cáceres',
 'EGRESADO', 2025, 1, 'POBREZA_EXTREMA', 0,
 1, 'PRESELECCIONADO', 'Requisitos acreditados correctamente.', 90.00, '2026-10-10 09:00:00'),

-- N.° 2: Ordinaria, 20 años, puntaje 84 (Límite exacto de preselección - Empate con N.° 4, postuló primero)
(2, 3, '70100002', 'Mateo Sebastián', 'Huamán Rojas', '2006-03-20', 20,
 'PERUANA', 'mateo.huaman@correo.pe', '987100002', 'Lima', 'I.E. Alfonso Ugarte',
 'EGRESADO', 2023, 1, 'POBREZA', 0,
 1, 'POSTULO A SELECCION', 'Documentación conforme.', 84.00, '2026-10-10 10:15:00'),

-- N.° 3: Ordinaria, 21 años, puntaje 83 (Un punto por debajo del límite)
(3, 4, '70100003', 'Camila Fernanda', 'Flores Vargas', '2005-05-10', 21,
 'PERUANA', 'camila.flores@correo.pe', '987100003', 'Cusco', 'I.E. Inca Garcilaso de la Vega',
 'EGRESADO', 2023, 1, 'POBREZA', 0,
 1, 'NO PRESELECCIONADO', 'Cumple requisitos de inscripción.', 83.00, '2026-10-10 11:30:00'),

-- N.° 4: Ordinaria, 19 años, puntaje 84 (Empate con el postulante 2, postuló segundo)
(4, 5, '70100004', 'Diego Alejandro', 'Paredes Condori', '2007-02-14', 19,
 'PERUANA', 'diego.paredes@correo.pe', '987100004', 'Junín', 'I.E. Santa Isabel',
 'EGRESADO', 2024, 1, 'POBREZA_EXTREMA', 0,
 1, 'POSTULO A SELECCION', 'Documentación conforme.', 84.00, '2026-10-11 08:45:00'),

-- N.° 5: Huallaga, 20 años, puntaje 70 (Modalidad especial aprobada)
(5, 6, '70100005', 'Lucía Milagros', 'Salazar Tello', '2006-06-01', 20,
 'PERUANA', 'lucia.salazar@correo.pe', '987100005', 'Huánuco', 'I.E. Gómez Arias Dávila',
 'EGRESADO', 2023, 1, 'VULNERABLE', 0,
 2, 'POSTULO A SELECCION', 'Acredita residencia en zona Huallaga.', 70.00, '2026-10-11 09:20:00'),

-- N.° 6: Vraem, 21 años, puntaje 66 (Límite exacto en modalidad especial)
(6, 7, '70100006', 'Rodrigo Gabriel', 'Cárdenas Yucra', '2005-07-18', 21,
 'PERUANA', 'rodrigo.cardenas@correo.pe', '987100006', 'Ayacucho', 'I.E. San Juan de la Frontera',
 'EGRESADO', 2022, 0, 'VULNERABLE', 0,
 3, 'PRESELECCIONADO', 'Acredita residencia en distrito VRAEM.', 66.00, '2026-10-11 14:10:00'),

-- N.° 7: Protección, 20 años, puntaje 65 (Un punto por debajo en modalidad especial)
(7, 8, '70100007', 'Ariana Valentina', 'Chávez Medina', '2006-01-25', 20,
 'PERUANA', 'ariana.chavez@correo.pe', '987100007', 'Arequipa', 'I.E. Juana Cervantes',
 'EGRESADO', 2023, 0, 'VULNERABLE', 0,
 4, 'NO PRESELECCIONADO', 'Acredita medida de protección estatal.', 65.00, '2026-10-12 09:00:00'),

-- N.° 8: Repared, 35 años, puntaje 66 (Sin límite de edad)
(8, 9, '70100008', 'Héctor Manuel', 'Gutiérrez Palomino', '1991-03-12', 35,
 'PERUANA', 'hector.gutierrez@correo.pe', '987100008', 'Ayacucho', 'I.E. Nuestra Señora de las Mercedes',
 'EGRESADO', 2008, 0, 'VULNERABLE', 0,
 5, 'POSTULO A SELECCION', 'Inscrito en el Registro Único de Víctimas (REBRED/REPARED).', 66.00, '2026-10-12 11:00:00'),

-- N.° 9: Comunidades Nativas Amazónicas, 24 años, puntaje 80 (Sin límite de edad)
(9, 10, '70100009', 'Katia Nayeli', 'Asháninka Shori', '2002-08-05', 24,
 'PERUANA', 'katia.shori@correo.pe', '987100009', 'Ucayali', 'I.E.B. Comunidad Nativa Yarinacocha',
 'EGRESADO', 2019, 1, 'POBREZA_EXTREMA', 0,
 6, 'POSTULO A SELECCION', 'Constancia de pertenencia a Comunidad Nativa Amazónica.', 80.00, '2026-10-13 10:30:00'),

-- N.° 10: Pueblo Afroperuano, 19 años, puntaje 55 (No alcanza el puntaje)
(10, 11, '70100010', 'Joaquín Eduardo', 'Carrillo Zambrano', '2007-05-19', 19,
 'PERUANA', 'joaquin.carrillo@correo.pe', '987100010', 'Ica', 'I.E. José Pardo y Barreda',
 'EGRESADO', 2024, 0, 'VULNERABLE', 0,
 7, 'NO PRESELECCIONADO', 'Acredita pertenencia a organización afroperuana.', 55.00, '2026-10-13 15:40:00'),

-- N.° 11: Educación Intercultural Bilingüe, 27 años, puntaje 75 (Sin límite de edad)
(11, 12, '70100011', 'Gladys Yaneth', 'Mamani Ccahuana', '1999-04-02', 27,
 'PERUANA', 'gladys.mamani@correo.pe', '987100011', 'Puno', 'I.E. Gran Unidad Escolar San Carlos',
 'EGRESADO', 2016, 1, 'POBREZA', 0,
 8, 'PRESELECCIONADO', 'Dominio acreditado de lengua originaria Quechua.', 75.00, '2026-10-14 09:15:00'),

-- N.° 12: Fuerzas Armadas, 30 años, puntaje 84 (Límite de edad de la modalidad: < 31 años)
(12, 13, '70100012', 'Carlos Alberto', 'Ramos Espinoza', '1996-06-10', 30,
 'PERUANA', 'carlos.ramos@correo.pe', '987100012', 'Piura', 'I.E. San Miguel de Piura',
 'EGRESADO', 2013, 0, 'VULNERABLE', 0,
 9, 'PRESELECCIONADO', 'Licenciado del Servicio Militar Voluntario.', 84.00, '2026-10-14 16:00:00');

-- -----------------------------------------------------------------------------
-- 7. Postulaciones a Selección de Prueba (Para comprobar CP-15: Empate en 1 vacante)
-- -----------------------------------------------------------------------------
INSERT INTO postulacion_seleccion (id_postulacion, id_postulante, id_programa, momento, fecha_hora, orden_merito, resultado) VALUES
(1, 2, 1, 1, '2027-01-10 08:30:00.000', NULL, 'PENDIENTE'),
(2, 4, 1, 1, '2027-01-10 09:15:00.000', NULL, 'PENDIENTE'),
(3, 5, 2, 1, '2027-01-11 10:00:00.000', NULL, 'PENDIENTE'),
(4, 8, 5, 1, '2027-01-12 11:20:00.000', NULL, 'PENDIENTE'),
(5, 9, 5, 1, '2027-01-12 14:45:00.000', NULL, 'PENDIENTE');

-- -----------------------------------------------------------------------------
-- 8. Registro inicial en bitácora
-- -----------------------------------------------------------------------------
INSERT INTO bitacora_evento (fecha_hora, tipo_evento, usuario, detalle) VALUES
(CURRENT_TIMESTAMP, 'SISTEMA', 'admin', 'Inicialización de base de datos SIBECA-18 en PostgreSQL (pgAdmin 4) con 9 modalidades y 12 postulantes de prueba.');

-- -----------------------------------------------------------------------------
-- 9. Sincronización de secuencias IDENTITY de PostgreSQL tras inserción con IDs explícitos
-- -----------------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('usuario', 'id_usuario'), COALESCE((SELECT MAX(id_usuario) FROM usuario), 1), true);
SELECT setval(pg_get_serial_sequence('modalidad', 'id_modalidad'), COALESCE((SELECT MAX(id_modalidad) FROM modalidad), 1), true);
SELECT setval(pg_get_serial_sequence('etapa', 'id_etapa'), COALESCE((SELECT MAX(id_etapa) FROM etapa), 1), true);
SELECT setval(pg_get_serial_sequence('institucion', 'id_institucion'), COALESCE((SELECT MAX(id_institucion) FROM institucion), 1), true);
SELECT setval(pg_get_serial_sequence('programa_estudio', 'id_programa'), COALESCE((SELECT MAX(id_programa) FROM programa_estudio), 1), true);
SELECT setval(pg_get_serial_sequence('postulante', 'id_postulante'), COALESCE((SELECT MAX(id_postulante) FROM postulante), 1), true);
SELECT setval(pg_get_serial_sequence('postulacion_seleccion', 'id_postulacion'), COALESCE((SELECT MAX(id_postulacion) FROM postulacion_seleccion), 1), true);
SELECT setval(pg_get_serial_sequence('bitacora_evento', 'id_evento'), COALESCE((SELECT MAX(id_evento) FROM bitacora_evento), 1), true);
