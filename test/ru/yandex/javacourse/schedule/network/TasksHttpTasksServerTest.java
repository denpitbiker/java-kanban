package ru.yandex.javacourse.schedule.network;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.network.gson.typetoken.TaskListTypeToken;
import ru.yandex.javacourse.schedule.network.handler.TasksHttpHandler;
import ru.yandex.javacourse.schedule.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TasksHttpTasksServerTest extends HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    @DisplayName("Проверка успешного получения задач")
    public void GET_tasks_TasksListCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(TaskStubs.TASK_STUB_3);
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(JsonParser.parseString(response.body()), new TaskListTypeToken().getType());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(3, tasks.size(), "size should be 3!");
        assertEquals(3, tasks.getLast().getId(), "last task in list should have id 3");
    }

    @Test
    @DisplayName("Проверка успешного получения задачи по id")
    public void GET_tasks_id_TaskWithIdCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        manager.addNewTask(TaskStubs.TASK_STUB_3);
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH + '/' + TaskStubs.TASK_STUB_1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(JsonParser.parseString(response.body()), Task.class);
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(1, task.getId(), "task id should be = 1");
    }

    @Test
    @DisplayName("Проверка получения задачи по несуществующему id")
    public void GET_tasks_id_TaskWithUnknownIdCodeNotFound() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        manager.addNewTask(TaskStubs.TASK_STUB_3);
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH + '/' + TaskStubs.TASK_STUB_2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_FOUND.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка добавления новой задачи")
    public void POST_tasks_TaskAddedCodeSuccessCreate() throws IOException, InterruptedException {
        // given
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH);
        String taskJson = gson.toJson(TaskStubs.TASK_STUB_2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS_CREATE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка обновления задачи")
    public void POST_tasks_TaskUpdatedCodeSuccessCreate() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        Task updatedTask = TaskStubs.TASK_STUB_2.clone();
        updatedTask.setName(TaskStubs.TASK_NAME_1);
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH);
        String taskJson = gson.toJson(updatedTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS_CREATE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка получения ошибки при попытке добавить пересекающуюся задачу")
    public void POST_tasks_TasksIntersectionCodeNotAcceptable() throws IOException, InterruptedException {
        // given
        Task longExistingTask = TaskStubs.TASK_STUB_2.clone();
        longExistingTask.setDuration(Duration.ofDays(100));
        manager.addNewTask(longExistingTask);
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH);
        String taskJson = gson.toJson(TaskStubs.TASK_STUB_3);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_ACCEPTABLE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка удаления задачи")
    public void DELETE_tasks_id_TaskDeletedCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        URI url = URI.create(SERVER_PATH + TasksHttpHandler.PATH + '/' + TaskStubs.TASK_STUB_2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
    }
}
