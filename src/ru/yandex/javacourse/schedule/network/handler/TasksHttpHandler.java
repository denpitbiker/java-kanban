package ru.yandex.javacourse.schedule.network.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.manager.exception.TaskNotFoundException;
import ru.yandex.javacourse.schedule.network.NetworkRequestMethod;
import ru.yandex.javacourse.schedule.network.gson.ParseHelper;
import ru.yandex.javacourse.schedule.tasks.Task;

import java.io.IOException;

public class TasksHttpHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;
    private final ParseHelper<Task> parseHelper = new ParseHelper<>();

    public TasksHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] segments = getPathSegments(exchange);
        if (method.equals(NetworkRequestMethod.GET.name)) {
            handleGet(exchange, segments);
        } else if (method.equals(NetworkRequestMethod.POST.name)) {
            handlePost(exchange, segments);
        } else if (method.equals(NetworkRequestMethod.DELETE.name)) {
            handleDelete(exchange, segments);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String[] segments) throws IOException {
        switch (segments.length) {
            case 2: {
                sendText(exchange, gson.toJson(manager.getTasks()));
                break;
            }
            case 3: {
                try {
                    sendText(exchange, gson.toJson(manager.getTask(Integer.parseInt(segments[2]))));
                } catch (TaskNotFoundException e) {
                    sendNotFound(exchange);
                } catch (Exception e) {
                    sendInternalError(exchange);
                }
                break;
            }
            default: {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange, String[] segments) throws IOException {
        if (segments.length == 2) {
            try {
                Task task = parseHelper.parse(exchange, gson, Task.class);
                try {
                    if (task.getId() != null) {
                        manager.getTask(task.getId());
                        manager.updateTask(task);
                    } else {
                        manager.addNewTask(task);
                    }
                    sendSuccessCreation(exchange);
                } catch (TaskNotFoundException e) {
                    if (manager.addNewTask(task) == null) {
                        sendHasIntersections(exchange);
                    } else {
                        sendSuccessCreation(exchange);
                    }
                }
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] segments) throws IOException {
        if (segments.length == 3) {
            try {
                manager.deleteTask(Integer.parseInt(segments[2]));
                sendSuccessCode(exchange);
            } catch (TaskNotFoundException e) {
                sendNotFound(exchange);
            } catch (Exception e) {
                sendInternalError(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    public static final String PATH = "/tasks";
}
