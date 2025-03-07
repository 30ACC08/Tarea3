-- Crear la tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL
);

-- Crear la tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(64) NOT NULL UNIQUE
);

-- Crear la tabla intermedia para la relación muchos a muchos entre usuarios y roles
CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id BIGINT,
    rol_id BIGINT,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insertar roles en la tabla roles (solo si no existen ya)
INSERT INTO roles (nombre)
SELECT 'ROLE_ADMIN' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ROLE_ADMIN');

INSERT INTO roles (nombre)
SELECT 'ROLE_USER' 
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ROLE_USER');

-- Insertar el usuario admin (solo si no existe)
INSERT INTO usuarios (nombre, email, password)
SELECT 'admin', 'admin@gmail.com', '$2a$12$9Sars17nXq0UYeX8qh54lO8MKi964ejmWefdi/9x4flwOcHPAS4.e'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@gmail.com');

-- Asignar el rol ADMIN al usuario Antonio (solo si no tiene ya ese rol)
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.email = 'admin@gmail.com' AND r.nombre = 'ROLE_ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM usuarios_roles ur 
    WHERE ur.usuario_id = u.id AND ur.rol_id = r.id
);