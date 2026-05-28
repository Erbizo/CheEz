CREATE TABLE jugadores (
    alias VARCHAR(8) PRIMARY KEY,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE solicitudes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alias_emisor VARCHAR(8),
    alias_receptor VARCHAR(8),
    estado ENUM('pendiente', 'aceptada', 'rechazada') DEFAULT 'pendiente',
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alias_emisor) REFERENCES jugadores(alias) ON DELETE CASCADE,
    FOREIGN KEY (alias_receptor) REFERENCES jugadores(alias) ON DELETE CASCADE
);

CREATE TABLE partidas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alias_blancas VARCHAR(8) NULL,
    alias_negras VARCHAR(8) NULL,
    fecha_hora_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resultado ENUM('blancas', 'negras', 'tablas', 'en_curso') DEFAULT 'en_curso',
    FOREIGN KEY (alias_blancas) REFERENCES jugadores(alias) ON DELETE SET NULL,
    FOREIGN KEY (alias_negras) REFERENCES jugadores(alias) ON DELETE SET NULL
);

CREATE TABLE movimientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_partida INT,
    orden INT,
    movimiento VARCHAR(10),
    FOREIGN KEY (id_partida) REFERENCES partidas(id) ON DELETE CASCADE
);