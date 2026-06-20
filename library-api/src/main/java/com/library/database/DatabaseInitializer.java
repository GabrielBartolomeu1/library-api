package com.library.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        try (Statement stmt = connection.createStatement()) {
            createTables(stmt);
            System.out.println("[DB] Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco de dados: " + e.getMessage(), e);
        }
    }

    private void createTables(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS authors (
                id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                name        VARCHAR(255) NOT NULL,
                nationality VARCHAR(100),
                email       VARCHAR(255) UNIQUE,
                biography   CLOB,
                created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS books (
                id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                title            VARCHAR(255) NOT NULL,
                isbn             VARCHAR(20) UNIQUE,
                publication_year INT,
                price            DECIMAL(10,2),
                pages            INT,
                language         VARCHAR(50),
                author_id        BIGINT NOT NULL,
                created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS categories (
                id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                name        VARCHAR(100) NOT NULL UNIQUE,
                description VARCHAR(500),
                created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS book_categories (
                book_id     BIGINT NOT NULL,
                category_id BIGINT NOT NULL,
                PRIMARY KEY (book_id, category_id),
                CONSTRAINT fk_bc_book     FOREIGN KEY (book_id)     REFERENCES books(id)      ON DELETE CASCADE,
                CONSTRAINT fk_bc_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
            )
        """);

        System.out.println("[DB] Tabelas criadas/verificadas.");
    }
}