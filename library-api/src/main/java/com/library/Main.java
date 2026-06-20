package com.library;

import com.library.controller.AuthorHandler;
import com.library.controller.BookHandler;
import com.library.controller.CategoryHandler;
import com.library.database.DatabaseConnection;
import com.library.database.DatabaseInitializer;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.CategoryService;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        new File("data").mkdirs();

        var dbConn = DatabaseConnection.getInstance();
        new DatabaseInitializer(dbConn.getConnection()).initialize();

        var authorRepository   = new AuthorRepository();
        var bookRepository     = new BookRepository();
        var categoryRepository = new CategoryRepository();

        var authorService   = new AuthorService(authorRepository, bookRepository);
        var categoryService = new CategoryService(categoryRepository);
        var bookService     = new BookService(bookRepository, authorRepository, categoryRepository);

        var authorHandler   = new AuthorHandler(authorService);
        var categoryHandler = new CategoryHandler(categoryService);
        var bookHandler     = new BookHandler(bookService);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/authors",    authorHandler);
        server.createContext("/books",      bookHandler);
        server.createContext("/categories", categoryHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[APP] Encerrando servidor...");
            server.stop(1);
            dbConn.closeConnection();
            System.out.println("[APP] Servidor encerrado.");
        }));

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("""
            ╔══════════════════════════════════════════╗
            ║        Library REST API iniciada!        ║
            ╠══════════════════════════════════════════╣
            ║  Porta  : %d                           ║
            ║  Banco  : H2 Embedded (./data/)          ║
            ║                                          ║
            ║  Endpoints disponíveis:                  ║
            ║  GET  /authors                           ║
            ║  GET  /authors/{id}                      ║
            ║  GET  /authors/{id}/books                ║
            ║  POST /authors                           ║
            ║  PUT  /authors/{id}                      ║
            ║  DEL  /authors/{id}                      ║
            ║                                          ║
            ║  GET  /books                             ║
            ║  GET  /books/{id}                        ║
            ║  POST /books                             ║
            ║  PUT  /books/{id}                        ║
            ║  DEL  /books/{id}                        ║
            ║  GET  /books/{id}/categories             ║
            ║  POST /books/{id}/categories/{catId}     ║
            ║  DEL  /books/{id}/categories/{catId}     ║
            ║                                          ║
            ║  GET  /categories                        ║
            ║  GET  /categories/{id}                   ║
            ║  POST /categories                        ║
            ║  PUT  /categories/{id}                   ║
            ║  DEL  /categories/{id}                   ║
            ╚══════════════════════════════════════════╝
            """.formatted(PORT));
    }
}
