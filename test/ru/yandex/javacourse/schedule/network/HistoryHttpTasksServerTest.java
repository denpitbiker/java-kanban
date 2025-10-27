package ru.yandex.javacourse.schedule.network;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.network.gson.typetoken.TaskListTypeToken;
import ru.yandex.javacourse.schedule.network.handler.HistoryHttpHandler;
import ru.yandex.javacourse.schedule.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHttpTasksServerTest extends HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String WRONG_SUBPATH = "/1";

    @Test
    @DisplayName("Проверка успешного получения истории просмотра задач")
    public void GET_history_HistoryListCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(TaskStubs.TASK_STUB_3);
        manager.getTask(1);
        manager.getTask(3);
        manager.getTask(2);
        URI url = URI.create(SERVER_PATH + HistoryHttpHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(JsonParser.parseString(response.body()), new TaskListTypeToken().getType());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(3, tasks.size(), "history size should be 3!");
        assertEquals(2, tasks.getLast().getId(), "Last task in history should have id 2");
    }

    @Test
    @DisplayName("Проверка ошибки получения истории просмотра задач")
    public void GET_history_CodeNotFound() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(TaskStubs.TASK_STUB_3);
        manager.getTask(1);
        manager.getTask(3);
        manager.getTask(2);
        URI wrongUrl = URI.create(SERVER_PATH + HistoryHttpHandler.PATH + WRONG_SUBPATH);
        HttpRequest request = HttpRequest.newBuilder().uri(wrongUrl).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_FOUND.statusCode, response.statusCode());
    }
}
