package ru.yandex.javacourse.schedule.network.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.network.NetworkRequestMethod;

import java.io.IOException;

public class PrioritizedHttpHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals(NetworkRequestMethod.GET.name)) {
            if (getPathSegments(exchange).length == 2) {
                sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    public static final String PATH = "/prioritized";
}