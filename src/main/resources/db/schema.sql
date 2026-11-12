-- HW Moving Services: esquema para MySQL 8
-- Uso: mysql -u root -p < src/main/resources/db/schema.sql
CREATE DATABASE IF NOT EXISTS hw_moving_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hw_moving_db;

CREATE TABLE IF NOT EXISTS cuenta (
    id_cuenta         VARCHAR(20)  PRIMARY KEY,
    email             VARCHAR(190) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    rol               VARCHAR(20)  NOT NULL,
    id_perfil         VARCHAR(20)  NOT NULL UNIQUE,
    activa            BOOLEAN      NOT NULL,
    correo_verificado BOOLEAN      NOT NULL,
    fecha_creacion    DATETIME     NOT NULL
);

CREATE TABLE IF NOT EXISTS token_cuenta (
    hash_token VARCHAR(64) PRIMARY KEY,
    id_cuenta  VARCHAR(20) NOT NULL,
    tipo       VARCHAR(30) NOT NULL,
    expira     DATETIME    NOT NULL,
    usado      BOOLEAN     NOT NULL,
    CONSTRAINT fk_token_cuenta FOREIGN KEY (id_cuenta) REFERENCES cuenta (id_cuenta)
);

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente VARCHAR(20)  PRIMARY KEY,
    nombre     VARCHAR(120),
    cedula     VARCHAR(20),
    telefono   VARCHAR(15),
    email      VARCHAR(190) NOT NULL,
    direccion  VARCHAR(190)
);

CREATE TABLE IF NOT EXISTS empleado (
    id_empleado          VARCHAR(20)  PRIMARY KEY,
    nombre               VARCHAR(120) NOT NULL,
    cedula               VARCHAR(20)  NOT NULL UNIQUE,
    telefono             VARCHAR(15)  NOT NULL,
    email                VARCHAR(190) NOT NULL,
    cargo                VARCHAR(20)  NOT NULL,
    turno                VARCHAR(30),
    edad                 INT,
    sexo                 VARCHAR(20),
    estado_civil         VARCHAR(30),
    eps                  VARCHAR(60),
    tipo_licencia        VARCHAR(10),
    vencimiento_licencia DATE,
    talla_dotacion       VARCHAR(20),
    estado               VARCHAR(20)  NOT NULL,
    suma_calificaciones  INT          NOT NULL,
    total_calificaciones INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS vehiculo (
    placa                     VARCHAR(10)  PRIMARY KEY,
    tipo                      VARCHAR(20)  NOT NULL,
    modelo                    VARCHAR(60)  NOT NULL,
    capacidad_carga_kg        DOUBLE       NOT NULL,
    capacidad_volumen_m3      DOUBLE       NOT NULL,
    vencimiento_soat          DATE         NOT NULL,
    vencimiento_tecnomecanica DATE         NOT NULL,
    estado                    VARCHAR(30)  NOT NULL
);

CREATE TABLE IF NOT EXISTS vehiculo_conductor (
    placa       VARCHAR(10) NOT NULL,
    id_empleado VARCHAR(20) NOT NULL,
    PRIMARY KEY (placa, id_empleado),
    CONSTRAINT fk_vc_vehiculo FOREIGN KEY (placa) REFERENCES vehiculo (placa),
    CONSTRAINT fk_vc_empleado FOREIGN KEY (id_empleado) REFERENCES empleado (id_empleado)
);

CREATE TABLE IF NOT EXISTS restriccion_pico_placa (
    id            VARCHAR(20) PRIMARY KEY,
    dia           VARCHAR(10) NOT NULL,
    digitos       VARCHAR(30) NOT NULL,
    hora_inicio   TIME        NOT NULL,
    hora_fin      TIME        NOT NULL,
    vigente_desde DATE        NOT NULL,
    vigente_hasta DATE        NOT NULL
);

CREATE TABLE IF NOT EXISTS solicitud_servicio (
    id_solicitud      VARCHAR(20)   PRIMARY KEY,
    id_cliente        VARCHAR(20)   NOT NULL,
    tipo_servicio     VARCHAR(20)   NOT NULL,
    estado            VARCHAR(20)   NOT NULL,
    fecha             DATE          NOT NULL,
    origen_municipio  VARCHAR(20)   NOT NULL,
    origen_direccion  VARCHAR(190)  NOT NULL,
    origen_piso       INT           NOT NULL,
    origen_ascensor   BOOLEAN       NOT NULL,
    destino_municipio VARCHAR(20)   NOT NULL,
    destino_direccion VARCHAR(190)  NOT NULL,
    destino_piso      INT           NOT NULL,
    destino_ascensor  BOOLEAN       NOT NULL,
    volumen_m3        DOUBLE        NOT NULL,
    enseres           VARCHAR(500),
    tipo_carga        VARCHAR(20)   NOT NULL,
    ayudantes         INT           NOT NULL,
    tarifa_base       DECIMAL(14,0) NOT NULL,
    recargo_distancia DECIMAL(14,0) NOT NULL,
    recargo_pisos     DECIMAL(14,0) NOT NULL,
    recargo_personal  DECIMAL(14,0) NOT NULL,
    total             DECIMAL(14,0) NOT NULL,
    vehiculo_sugerido VARCHAR(20)   NOT NULL,
    distancia_km      DOUBLE        NOT NULL,
    duracion_minutos  INT           NOT NULL,
    fecha_creacion    DATETIME      NOT NULL,
    CONSTRAINT fk_solicitud_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
);

CREATE TABLE IF NOT EXISTS reserva (
    id_reserva       VARCHAR(20)   PRIMARY KEY,
    id_solicitud     VARCHAR(20)   NOT NULL UNIQUE,
    id_cliente       VARCHAR(20)   NOT NULL,
    fecha_reserva    DATETIME      NOT NULL,
    fecha_servicio   DATETIME      NOT NULL,
    fecha_fin        DATETIME      NOT NULL,
    duracion_minutos INT           NOT NULL,
    total            DECIMAL(14,0) NOT NULL,
    estado           VARCHAR(30)   NOT NULL,
    placa_vehiculo   VARCHAR(10),
    id_conductor     VARCHAR(20),
    CONSTRAINT fk_reserva_solicitud FOREIGN KEY (id_solicitud) REFERENCES solicitud_servicio (id_solicitud),
    CONSTRAINT fk_reserva_cliente   FOREIGN KEY (id_cliente)   REFERENCES cliente (id_cliente),
    CONSTRAINT fk_reserva_vehiculo  FOREIGN KEY (placa_vehiculo) REFERENCES vehiculo (placa),
    CONSTRAINT fk_reserva_conductor FOREIGN KEY (id_conductor) REFERENCES empleado (id_empleado)
);

CREATE TABLE IF NOT EXISTS reserva_auxiliar (
    id_reserva  VARCHAR(20) NOT NULL,
    id_empleado VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_reserva, id_empleado),
    CONSTRAINT fk_ra_reserva  FOREIGN KEY (id_reserva)  REFERENCES reserva (id_reserva),
    CONSTRAINT fk_ra_empleado FOREIGN KEY (id_empleado) REFERENCES empleado (id_empleado)
);

CREATE TABLE IF NOT EXISTS pago (
    id_pago     VARCHAR(20)   PRIMARY KEY,
    id_reserva  VARCHAR(20)   NOT NULL,
    monto       DECIMAL(14,0) NOT NULL,
    tipo_pago   VARCHAR(20)   NOT NULL,
    metodo_pago VARCHAR(20)   NOT NULL,
    estado_pago VARCHAR(20)   NOT NULL,
    referencia  VARCHAR(80),
    fecha       DATETIME      NOT NULL,
    CONSTRAINT fk_pago_reserva FOREIGN KEY (id_reserva) REFERENCES reserva (id_reserva)
);

CREATE TABLE IF NOT EXISTS evento_servicio (
    id_evento       VARCHAR(20) PRIMARY KEY,
    id_reserva      VARCHAR(20) NOT NULL,
    estado          VARCHAR(30) NOT NULL,
    fecha_hora      DATETIME(3) NOT NULL,
    id_cuenta_actor VARCHAR(20) NOT NULL,
    CONSTRAINT fk_evento_reserva FOREIGN KEY (id_reserva) REFERENCES reserva (id_reserva)
);

CREATE TABLE IF NOT EXISTS evaluacion (
    id_evaluacion       VARCHAR(20) PRIMARY KEY,
    id_reserva          VARCHAR(20) NOT NULL UNIQUE,
    puntuacion_servicio INT         NOT NULL,
    puntuacion_personal INT         NOT NULL,
    comentario          VARCHAR(500),
    fecha               DATETIME    NOT NULL,
    CONSTRAINT fk_evaluacion_reserva FOREIGN KEY (id_reserva) REFERENCES reserva (id_reserva)
);
