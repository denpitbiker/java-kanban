package ru.yandex.javacourse.schedule.network;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.javacourse.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacourse.schedule.manager.TaskManager;

import java.io.IOException;

public abstract class HttpTaskServerTest {
    protected TaskManager manager = new InMemoryTaskManager();
    protected HttpTaskServer taskServer = new HttpTaskServer(manager);
    protected Gson gson = HttpTaskServer.getGson();

    protected static final String SERVER_PATH = "http://localhost:8080";

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}
