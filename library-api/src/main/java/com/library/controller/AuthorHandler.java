package com.library.controller;

import com.library.model.Author;
import com.library.service.AuthorService;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class AuthorHandler extends BaseHandler {

    private static final String BASE = "/authors";
    private final AuthorService authorService;

    public AuthorHandler(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    protected void dispatch(HttpExchange exchange) throws IOException {
        String method = getMethod(exchange);
        String path   = getPath(exchange);

        if (method.equals("GET") && path.matches(BASE + "/\\d+/books")) {
            Long id = extractId(path.replace("/books", ""), BASE + "/");
            sendJsonResponse(exchange, 200, authorService.findBooksByAuthor(id));
            return;
        }

        boolean hasId = path.matches(BASE + "/\\d+");

        switch (method) {
            case "GET" -> {
                if (hasId) {
                    Long id = extractId(path, BASE + "/");
                    sendJsonResponse(exchange, 200, authorService.findById(id));
                } else {
                    List<Author> authors = authorService.findAll();
                    sendJsonResponse(exchange, 200, authors);
                }
            }
            case "POST" -> {
                Author body = JsonUtil.fromJson(exchange.getRequestBody(), Author.class);
                Author created = authorService.create(body);
                sendJsonResponse(exchange, 201, created, "Autor criado com sucesso.");
            }
            case "PUT" -> {
                Long id = extractId(path, BASE + "/");
                Author body = JsonUtil.fromJson(exchange.getRequestBody(), Author.class);
                Author updated = authorService.update(id, body);
                sendJsonResponse(exchange, 200, updated, "Autor atualizado com sucesso.");
            }
            case "DELETE" -> {
                Long id = extractId(path, BASE + "/");
                authorService.delete(id);
                sendJsonResponse(exchange, 200, null, "Autor removido com sucesso.");
            }
            default -> sendErrorResponse(exchange, 405, "Método não permitido: " + method);
        }
    }
}
