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

