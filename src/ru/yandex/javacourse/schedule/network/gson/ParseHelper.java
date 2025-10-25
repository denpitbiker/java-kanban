package ru.yandex.javacourse.schedule.network.gson;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ParseHelper<T> {

    public T parse(HttpExchange exchange, Gson gson, Class<T> tClass) throws IOException {
        return gson.fromJson(
                JsonParser.parseString(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)),
                tClass
        );
    }
}
