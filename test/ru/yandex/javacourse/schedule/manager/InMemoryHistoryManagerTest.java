package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void initHistoryManager() {
        // given
        historyManager = Managers.getDefaultHistoryManager();
    }

    @Test
    @DisplayName("Проверка что история пустая при создании")
    public void getHistory_NoTasksInHistory_EmptyHistory() {
        // then
        assertEquals(0, historyManager.getHistory().size(), "history should be empty!");
    }

    @Test
    @DisplayName("Проверка что в истории сохраняется только последнее обращение к задаче (нет дубликатов)")
    public void addTask_OnlyLastTaskEntranceSave_LastTaskEntranceSaved() {
        // given
        Task task = new Task(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null);
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.addTask(task);
        // then
        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().getFirst().getStatus(), "historic task should be changed");
        assertEquals(1, historyManager.getHistory().size(), "no task duplicate should exist");
    }

    @Test
    @DisplayName("Проверка создания истории просмотра задач")
    public void getHistory_AddTasksToHistoryManager_TasksAdded() {
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        historyManager.addTask(TaskStubs.TASK_STUB_2);
        historyManager.addTask(TaskStubs.TASK_STUB_3);
        // then
        assertEquals(3, historyManager.getHistory().size(), "history size should be equal to 3");
        assertEquals(2, historyManager.getHistory().get(1).getId(), "id should be equal to 2");
    }

    @Test
    @DisplayName("Проверка очистки истории просмотра задач")
    public void remove_RemoveTasksFromHistoryManager_TasksRemoved() {
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        historyManager.addTask(TaskStubs.TASK_STUB_2);
        historyManager.addTask(TaskStubs.TASK_STUB_3);
        historyManager.remove(TaskStubs.TASK_STUB_2.getId());
        // then
        assertEquals(2, historyManager.getHistory().size(), "history size should be equal to 2");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "id should be equal to 1");
        historyManager.remove(TaskStubs.TASK_STUB_1.getId());
        assertEquals(1, historyManager.getHistory().size(), "history size should be equal to 1");
        assertEquals(3, historyManager.getHistory().getFirst().getId(), "id should be equal to 3");
        historyManager.remove(TaskStubs.TASK_STUB_3.getId());
        assertEquals(0, historyManager.getHistory().size(), "history size should be equal to 0");
        historyManager.remove(TaskStubs.TASK_STUB_3.getId());
        assertEquals(0, historyManager.getHistory().size(), "history size should be equal to 0");
    }

    @Test
    @DisplayName("Проверка удаления задачи из начала")
    public void remove_RemoveFirstTaskFromHistoryManager_TasksRemoved() {
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        historyManager.addTask(TaskStubs.TASK_STUB_2);
        historyManager.addTask(TaskStubs.TASK_STUB_3);
        historyManager.remove(TaskStubs.TASK_STUB_1.getId());
        // then
        assertEquals(2, historyManager.getHistory().size(), "history size should be equal to 2");
        assertEquals(2, historyManager.getHistory().getFirst().getId(), "id should be equal to 2");
        assertEquals(3, historyManager.getHistory().getLast().getId(), "id should be equal to 3");
    }

    @Test
    @DisplayName("Проверка удаления задачи из середины")
    public void remove_RemoveMiddleTaskFromHistoryManager_TasksRemoved() {
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        historyManager.addTask(TaskStubs.TASK_STUB_2);
        historyManager.addTask(TaskStubs.TASK_STUB_3);
        historyManager.remove(TaskStubs.TASK_STUB_2.getId());
        // then
        assertEquals(2, historyManager.getHistory().size(), "history size should be equal to 2");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "id should be equal to 1");
        assertEquals(3, historyManager.getHistory().getLast().getId(), "id should be equal to 3");
    }

    @Test
    @DisplayName("Проверка удаления задачи из конца")
    public void remove_RemoveLastTaskFromHistoryManager_TasksRemoved() {
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        historyManager.addTask(TaskStubs.TASK_STUB_2);
        historyManager.addTask(TaskStubs.TASK_STUB_3);
        historyManager.remove(TaskStubs.TASK_STUB_3.getId());
        // then
        assertEquals(2, historyManager.getHistory().size(), "history size should be equal to 2");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "id should be equal to 1");
        assertEquals(2, historyManager.getHistory().getLast().getId(), "id should be equal to 2");
    }
}
