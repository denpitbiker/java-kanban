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
    @DisplayName("Проверка что в истории сохраняется только последнее обращение к задаче")
    public void addTask_OnlyLastTaskEntranceSave_LastTaskEntranceSaved() {
        // given
        Task task = new Task(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
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
    public void addTask_AddTasksToHistoryManager_TasksAdded() {
        // when
        historyManager.addTask(TaskStubs.TASK_STUB_1);
        historyManager.addTask(TaskStubs.TASK_STUB_2);
        historyManager.addTask(TaskStubs.TASK_STUB_3);
        // then
        assertEquals(3, historyManager.getHistory().size(), "history size should be equals 3");
        assertEquals(2, historyManager.getHistory().get(1).getId(), "id should be  equal 2");
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
        assertEquals(2, historyManager.getHistory().size(), "history size should be equals 2");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "id should be  equal 2");
        historyManager.remove(TaskStubs.TASK_STUB_1.getId());
        assertEquals(1, historyManager.getHistory().size(), "history size should be equals 1");
        assertEquals(3, historyManager.getHistory().getFirst().getId(), "id should be  equal 3");
        historyManager.remove(TaskStubs.TASK_STUB_3.getId());
        assertEquals(0, historyManager.getHistory().size(), "history size should be equals 0");
        historyManager.remove(TaskStubs.TASK_STUB_3.getId());
        assertEquals(0, historyManager.getHistory().size(), "history size should be equals 0");
    }

}
