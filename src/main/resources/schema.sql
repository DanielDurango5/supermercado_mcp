CREATE DATABASE IF NOT EXISTS supermercado_mcp
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE supermercado_mcp;

CREATE TABLE IF NOT EXISTS producto (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(60) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedido (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      cliente_nombre VARCHAR(120) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(30),
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pedido_item (
                                           id BIGINT NOT NULL AUTO_INCREMENT,
                                           pedido_id BIGINT NOT NULL,
                                           producto_id BIGINT NOT NULL,
                                           cantidad INT NOT NULL,
                                           precio_unitario DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
    ) ENGINE=InnoDB;

INSERT INTO producto (nombre, categoria, precio, stock) VALUES
                                                            ('Leche entera 1L', 'Lácteos', 4200, 50),
                                                            ('Huevos x30', 'Huevos', 17500, 20),
                                                            ('Arroz 1kg', 'Granos', 4800, 80),
                                                            ('Pan tajado', 'Panadería', 6500, 30),
                                                            ('Manzana roja kg', 'Frutas', 7900, 40),
                                                            ('Jabón líquido 500ml', 'Aseo', 12900, 25);

