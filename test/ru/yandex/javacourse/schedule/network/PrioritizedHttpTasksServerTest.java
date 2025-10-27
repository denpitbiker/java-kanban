package ru.yandex.javacourse.schedule.network;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.network.gson.typetoken.TaskListTypeToken;
import ru.yandex.javacourse.schedule.network.handler.PrioritizedHttpHandler;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHttpTasksServerTest extends HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String WRONG_SUBPATH = "/1";
    private final Subtask dateTimeSubtask = new Subtask(
            1,
            TaskStubs.TASK_NAME_1,
            TaskStubs.TASK_DESCRIPTION_1,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30,
            3
    );

    private final Epic epic = new Epic(
            3,
            TaskStubs.TASK_NAME_3,
            TaskStubs.TASK_DESCRIPTION_3
    );

    @Test
    @DisplayName("Проверка успешного получения задач по проиритету")
    public void GET_prioritized_PrioritizedListCodeSuccess() throws IOException, InterruptedException {
        // given
        Subtask notIntersectingSubtask = new Subtask(
                2,
                TaskStubs.TASK_NAME_1,
                TaskStubs.TASK_DESCRIPTION_1,
                TaskStatus.NEW,
                TaskStubs.DURATION_1D,
                TaskStubs.DATE_TIME_19_10_2025_12_30.plusDays(1),
                3
        );
        Task notIntersectingTask = new Task(
                4,
                TaskStubs.TASK_NAME_3,
                TaskStubs.TASK_DESCRIPTION_3,
                TaskStatus.NEW,
                TaskStubs.DURATION_1D,
                TaskStubs.DATE_TIME_19_10_2025_12_30.plusDays(5)
        );
        manager.addNewEpic(epic);
        manager.addNewTask(notIntersectingTask);
        manager.addNewSubtask(notIntersectingSubtask);
        manager.addNewSubtask(dateTimeSubtask);
        URI url = URI.create(SERVER_PATH + PrioritizedHttpHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(JsonParser.parseString(response.body()), new TaskListTypeToken().getType());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(3, tasks.size(), "prioritized size should be 3!");
        assertEquals(4, tasks.getLast().getId(), "last task in prioritized should have id 4");
    }

    @Test
    @DisplayName("Проверка ошибки получения задач по проиритету")
    public void GET_prioritized_CodeNotFound() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(TaskStubs.TASK_STUB_3);
        manager.getTask(1);
        manager.getTask(3);
        manager.getTask(2);
        URI wrongUrl = URI.create(SERVER_PATH + PrioritizedHttpHandler.PATH + WRONG_SUBPATH);
        HttpRequest request = HttpRequest.newBuilder().uri(wrongUrl).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_FOUND.statusCode, response.statusCode());
    }
}
