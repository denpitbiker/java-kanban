package ru.yandex.javacourse.schedule.network;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.network.gson.typetoken.SubtaskListTypeToken;
import ru.yandex.javacourse.schedule.network.handler.SubtasksHttpHandler;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtasksHttpTasksServerTest extends HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Subtask dateTimeSubtask = new Subtask(
            1,
            TaskStubs.TASK_NAME_1,
            TaskStubs.TASK_DESCRIPTION_1,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30,
            3
    );

    private final Subtask intersectingSubtask = new Subtask(
            2,
            TaskStubs.TASK_NAME_2,
            TaskStubs.TASK_DESCRIPTION_2,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30.plusHours(1),
            3
    );

    Subtask notIntersectingSubtask = new Subtask(
            2,
            TaskStubs.TASK_NAME_1,
            TaskStubs.TASK_DESCRIPTION_1,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30.plusDays(1),
            3
    );

    private final Epic epic = new Epic(
            3,
            TaskStubs.TASK_NAME_3,
            TaskStubs.TASK_DESCRIPTION_3
    );

    @Test
    @DisplayName("Проверка успешного получения подзадач")
    public void GET_subtasks_SubtasksListCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewSubtask(notIntersectingSubtask);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(JsonParser.parseString(response.body()), new SubtaskListTypeToken().getType());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(2, subtasks.size(), "size should be 2!");
        assertEquals(2, subtasks.getLast().getId(), "last subtask in list should have id 2");
    }

    @Test
    @DisplayName("Проверка успешного получения подзадачи по id")
    public void GET_subtasks_id_SubtaskWithIdCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewSubtask(notIntersectingSubtask);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH + '/' + dateTimeSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(JsonParser.parseString(response.body()), Subtask.class);
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(1, subtask.getId(), "subtask id should be = 1");
    }

    @Test
    @DisplayName("Проверка получения подзадачи по несуществующему id")
    public void GET_subtasks_id_SubtaskWithUnknownIdCodeNotFound() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        manager.addNewSubtask(dateTimeSubtask);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH + '/' + notIntersectingSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_FOUND.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка добавления новой подзадачи")
    public void POST_subtasks_SubtaskAddedCodeSuccessCreate() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH);
        String taskJson = gson.toJson(dateTimeSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS_CREATE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка обновления подзадачи")
    public void POST_subtasks_SubtaskUpdatedCodeSuccessCreate() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        manager.addNewSubtask(dateTimeSubtask);
        Subtask updatedSubtask = dateTimeSubtask.clone();
        updatedSubtask.setName(TaskStubs.TASK_NAME_3);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH);
        String taskJson = gson.toJson(updatedSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS_CREATE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка получения ошибки при попытке добавить пересекающуюся подзадачу")
    public void POST_subtasks_SubtasksIntersectionCodeNotAcceptable() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        manager.addNewSubtask(dateTimeSubtask);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH);
        String taskJson = gson.toJson(intersectingSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_ACCEPTABLE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка удаления подзадачи")
    public void DELETE_subtasks_id_SubtaskDeletedCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic);
        manager.addNewSubtask(dateTimeSubtask);
        URI url = URI.create(SERVER_PATH + SubtasksHttpHandler.PATH + '/' + dateTimeSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
    }
}
