package ru.yandex.javacourse.schedule.network.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.schedule.network.NetworkCode;
import ru.yandex.javacourse.schedule.network.NetworkHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add(NetworkHeader.CONTENT_TYPE.name, DEFAULT_CONTENT_TYPE);
        exchange.sendResponseHeaders(NetworkCode.SUCCESS.statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    void sendCode(HttpExchange exchange, NetworkCode code) throws IOException {
        exchange.sendResponseHeaders(code.statusCode, ZERO_RESPONSE_LENGTH);
        exchange.close();
    }

    String[] getPathSegments(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split(PATH_SEPARATOR);
    }

    void sendSuccessCode(HttpExchange exchange) throws IOException {
        sendCode(exchange, NetworkCode.SUCCESS);
    }

    void sendSuccessCreation(HttpExchange exchange) throws IOException {
        sendCode(exchange, NetworkCode.SUCCESS_CREATE);
    }

    void sendNotFound(HttpExchange exchange) throws IOException {
        sendCode(exchange, NetworkCode.NOT_FOUND);
    }

    void sendInternalError(HttpExchange exchange) throws IOException {
        sendCode(exchange, NetworkCode.INTERNAL_SERVER_ERROR);
    }

    void sendHasIntersections(HttpExchange exchange) throws IOException {
        sendCode(exchange, NetworkCode.NOT_ACCEPTABLE);
    }

    private static final int ZERO_RESPONSE_LENGTH = 0;
    private static final String DEFAULT_CONTENT_TYPE = "application/json;charset=utf-8";
    private static final String PATH_SEPARATOR = "/";
}
