package com.library.controller;

import com.library.exception.NotFoundException;
import com.library.exception.ValidationException;
import com.library.util.ApiResponse;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public abstract class BaseHandler implements HttpHandler {

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 204, "");
                return;
            }

            dispatch(exchange);

        } catch (NotFoundException e) {
            sendErrorResponse(exchange, 404, e.getMessage());
        } catch (ValidationException e) {
            sendErrorResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(exchange, 500, "Erro interno do servidor: " + e.getMessage());
        }
    }


    protected abstract void dispatch(HttpExchange exchange) throws IOException;


    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        ApiResponse<?> response = ApiResponse.success(data);
        sendResponse(exchange, statusCode, JsonUtil.toJson(response));
    }

    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Object data, String message) throws IOException {
        ApiResponse<?> response = ApiResponse.success(data, message);
        sendResponse(exchange, statusCode, JsonUtil.toJson(response));
    }

    protected void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        ApiResponse<?> response = ApiResponse.error(message);
        sendResponse(exchange, statusCode, JsonUtil.toJson(response));
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected Long extractId(String path, String basePath) {
        String idStr = path.substring(basePath.length()).replaceAll("/", "");
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID inválido: '" + idStr + "'");
        }
    }

    protected String getMethod(HttpExchange exchange) {
        return exchange.getRequestMethod().toUpperCase();
    }

    protected String getPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath();
    }
}
