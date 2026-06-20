package com.library.controller;

import com.library.model.Category;
import com.library.service.CategoryService;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CategoryHandler extends BaseHandler {

    private static final String BASE = "/categories";
    private final CategoryService categoryService;

    public CategoryHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    protected void dispatch(HttpExchange exchange) throws IOException {
        String method = getMethod(exchange);
        String path   = getPath(exchange);
        boolean hasId = path.matches(BASE + "/\\d+");

        switch (method) {
            case "GET" -> {
                if (hasId) {
                    Long id = extractId(path, BASE + "/");
                    sendJsonResponse(exchange, 200, categoryService.findById(id));
                } else {
                    sendJsonResponse(exchange, 200, categoryService.findAll());
                }
            }
            case "POST" -> {
                Category body = JsonUtil.fromJson(exchange.getRequestBody(), Category.class);
                Category created = categoryService.create(body);
                sendJsonResponse(exchange, 201, created, "Categoria criada com sucesso.");
            }
            case "PUT" -> {
                Long id = extractId(path, BASE + "/");
                Category body = JsonUtil.fromJson(exchange.getRequestBody(), Category.class);
                Category updated = categoryService.update(id, body);
                sendJsonResponse(exchange, 200, updated, "Categoria atualizada com sucesso.");
            }
            case "DELETE" -> {
                Long id = extractId(path, BASE + "/");
                categoryService.delete(id);
                sendJsonResponse(exchange, 200, null, "Categoria removida com sucesso.");
            }
            default -> sendErrorResponse(exchange, 405, "Método não permitido: " + method);
        }
    }
}
