package ru.yandex.javacourse.schedule.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.schedule.manager.Managers;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.network.gson.typeadapter.DurationTypeAdapter;
import ru.yandex.javacourse.schedule.network.gson.typeadapter.LocalDateTimeTypeAdapter;
import ru.yandex.javacourse.schedule.network.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int SERVER_PORT = 8080;
    private static final int SERVER_BACKLOG = 0;
    private static final int SERVER_STOP_DELAY_SECONDS = 1;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .serializeNulls()
            .create();

    private final TaskManager manager;
    private HttpServer server;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), SERVER_BACKLOG);
        setupContext();
        server.start();
    }

    public void stop() {
        server.stop(SERVER_STOP_DELAY_SECONDS);
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer(Managers.getInMemoryTaskManager()).start();
    }

    private void setupContext() {
        server.createContext(EpicsHttpHandler.PATH, new EpicsHttpHandler(manager, gson));
        server.createContext(HistoryHttpHandler.PATH, new HistoryHttpHandler(manager, gson));
        server.createContext(PrioritizedHttpHandler.PATH, new PrioritizedHttpHandler(manager, gson));
        server.createContext(SubtasksHttpHandler.PATH, new SubtasksHttpHandler(manager, gson));
        server.createContext(TasksHttpHandler.PATH, new TasksHttpHandler(manager, gson));
    }
}
