package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class BookHandler extends BaseHandler {

    private static final String BASE = "/books";
    private final BookService bookService;

    public BookHandler(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    protected void dispatch(HttpExchange exchange) throws IOException {
        String method = getMethod(exchange);
        String path   = getPath(exchange);

        if (path.matches(BASE + "/\\d+/categories/\\d+")) {
            long[] ids = extractTwoIds(path);
            handleCategoryAssociation(exchange, method, ids[0], ids[1]);
            return;
        }

        if (path.matches(BASE + "/\\d+/categories")) {
            Long bookId = extractId(path.replace("/categories", ""), BASE + "/");
            if (method.equals("GET")) {
                sendJsonResponse(exchange, 200, bookService.findCategoriesByBook(bookId));
            } else {
                sendErrorResponse(exchange, 405, "Método não permitido nesta rota.");
            }
            return;
        }

        boolean hasId = path.matches(BASE + "/\\d+");

        switch (method) {
            case "GET" -> {
                if (hasId) {
                    Long id = extractId(path, BASE + "/");
                    sendJsonResponse(exchange, 200, bookService.findById(id));
                } else {
                    sendJsonResponse(exchange, 200, bookService.findAll());
                }
            }
            case "POST" -> {
                Book body = JsonUtil.fromJson(exchange.getRequestBody(), Book.class);
                Book created = bookService.create(body);
                sendJsonResponse(exchange, 201, created, "Livro criado com sucesso.");
            }
            case "PUT" -> {
                Long id = extractId(path, BASE + "/");
                Book body = JsonUtil.fromJson(exchange.getRequestBody(), Book.class);
                Book updated = bookService.update(id, body);
                sendJsonResponse(exchange, 200, updated, "Livro atualizado com sucesso.");
            }
            case "DELETE" -> {
                Long id = extractId(path, BASE + "/");
                bookService.delete(id);
                sendJsonResponse(exchange, 200, null, "Livro removido com sucesso.");
            }
            default -> sendErrorResponse(exchange, 405, "Método não permitido: " + method);
        }
    }

    private void handleCategoryAssociation(HttpExchange exchange, String method,
                                            long bookId, long categoryId) throws IOException {
        switch (method) {
            case "POST" -> {
                Book updated = bookService.addCategory(bookId, categoryId);
                sendJsonResponse(exchange, 200, updated, "Categoria associada ao livro com sucesso.");
            }
            case "DELETE" -> {
                Book updated = bookService.removeCategory(bookId, categoryId);
                sendJsonResponse(exchange, 200, updated, "Categoria removida do livro com sucesso.");
            }
            default -> sendErrorResponse(exchange, 405, "Método não permitido nesta rota.");
        }
    }

    private long[] extractTwoIds(String path) {
        String[] parts = path.split("/");
        // path = "" / "books" / "3" / "categories" / "7"
        return new long[]{Long.parseLong(parts[2]), Long.parseLong(parts[4])};
    }
}
