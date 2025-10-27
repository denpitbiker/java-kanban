package ru.yandex.javacourse.schedule.network;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.network.gson.typetoken.EpicListTypeToken;
import ru.yandex.javacourse.schedule.network.gson.typetoken.SubtaskListTypeToken;
import ru.yandex.javacourse.schedule.network.handler.EpicsHttpHandler;
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

public class EpicsHttpTasksServerTest extends HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String EPIC_SUBTASKS_PATH = "/subtasks";
    private final Subtask dateTimeSubtask = new Subtask(
            1,
            TaskStubs.TASK_NAME_1,
            TaskStubs.TASK_DESCRIPTION_1,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30,
            3
    );

    private final Epic epic2 = new Epic(
            2,
            TaskStubs.TASK_NAME_2,
            TaskStubs.TASK_DESCRIPTION_2
    );

    private final Epic epic3 = new Epic(
            3,
            TaskStubs.TASK_NAME_3,
            TaskStubs.TASK_DESCRIPTION_3
    );

    @Test
    @DisplayName("Проверка успешного получения эпиков")
    public void GET_epics_EpicsListCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic2);
        manager.addNewEpic(epic3);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(JsonParser.parseString(response.body()), new EpicListTypeToken().getType());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(2, epics.size(), "size should be 2!");
        assertEquals(3, epics.getLast().getId(), "last epic in list should have id 3");
    }

    @Test
    @DisplayName("Проверка успешного получения эпика по id")
    public void GET_epics_id_EpicWithIdCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic2);
        manager.addNewEpic(epic3);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH + '/' + epic2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(JsonParser.parseString(response.body()), Epic.class);
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(2, epic.getId(), "epic id should be = 2");
    }

    @Test
    @DisplayName("Проверка получения эпика по несуществующему id")
    public void GET_epics_id_EpicWithUnknownIdCodeNotFound() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic2);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH + '/' + epic3.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_FOUND.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка получения подзадач по id эпика")
    public void GET_epics_id_subtasks_EpicSubtasksCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic3);
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewEpic(epic2);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH + '/' + epic3.getId() + EPIC_SUBTASKS_PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(JsonParser.parseString(response.body()), new SubtaskListTypeToken().getType());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
        assertEquals(1, subtasks.getFirst().getId(), "subtask id should be = 1");
    }

    @Test
    @DisplayName("Проверка получения подзадач по id несуществующего эпика")
    public void GET_epics_id_subtasks_EpicSubtasksCodeNotFound() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic3);
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewEpic(epic2);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH + '/' + 100 + EPIC_SUBTASKS_PATH);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.NOT_FOUND.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка добавления нового эпика")
    public void POST_epics_EpicAddedCodeSuccessCreate() throws IOException, InterruptedException {
        // given
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH);
        String epicJson = gson.toJson(epic2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS_CREATE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка обновления эпика")
    public void POST_epics_EpicUpdatedCodeSuccessCreate() throws IOException, InterruptedException {
        // given
        Epic updatedEpic = epic2.clone();
        updatedEpic.setName(TaskStubs.TASK_NAME_3);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH);
        String epicJson = gson.toJson(updatedEpic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS_CREATE.statusCode, response.statusCode());
    }

    @Test
    @DisplayName("Проверка удаления эпика")
    public void DELETE_epic_id_EpicDeletedCodeSuccess() throws IOException, InterruptedException {
        // given
        manager.addNewEpic(epic2);
        URI url = URI.create(SERVER_PATH + EpicsHttpHandler.PATH + '/' + epic2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        // when
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // then
        assertEquals(NetworkCode.SUCCESS.statusCode, response.statusCode());
    }
}
