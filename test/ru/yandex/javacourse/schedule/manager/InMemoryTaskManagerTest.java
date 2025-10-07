package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {

    TaskManager manager;

    @BeforeEach
    public void initManager() {
        manager = Managers.getDefaultTaskManager();
    }

    @Test
    @DisplayName("Проверка получения задачи по id из менеджера задач")
    public void getTask_GetTaskById_TaskFound() {
        // given
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        // when
        manager.addNewTask(task);
        Task byIdTask = manager.getTask(task.getId());
        // then
        assertEquals(task, byIdTask, "added task id should be found");
    }

    @Test
    @DisplayName("Проверка получения задачи по id из менеджера задач")
    public void addNewTask_AddTaskWIthNoId_IdGenerated() {
        // given
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        // when
        manager.addNewTask(task);
        Task addedTask = manager.getTasks().getFirst();
        // then
        assertEquals(task, addedTask, "added task id should be set");
    }

    @Test
    @DisplayName("Проверка добавления задачи с заданным id")
    public void addNewTask_AddTaskWithId_TaskAdded() {
        // when
        manager.addNewTask(TaskStubs.TASK_STUB_1);
        Task addedTask = manager.getTasks().getFirst();
        // then
        assertEquals(TaskStubs.TASK_STUB_1, addedTask, "predefined task id should be set");
    }

    @Test
    @DisplayName("Проверка, что задача не изменилась после добавления в менеджер задач")
    public void addNewTask_TaskNotChangedAfterAdding_TaskNotChanged() {
        // given
        int id = 1;
        TaskStatus status = TaskStatus.NEW;
        Task task1before = new Task(id, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, status);
        // when
        manager.addNewTask(task1before);
        Task task1after = manager.getTask(task1before.getId());
        // then
        assertEquals(id, task1after.getId());
        assertEquals(TaskStubs.TASK_DESCRIPTION_1, task1after.getDescription());
        assertEquals(status, task1after.getStatus());
        assertEquals(TaskStubs.TASK_NAME_1, task1after.getName());
    }

    @Test
    @DisplayName("Проверка добавления-удаления подзадач из эпика через менеджер задач")
    public void SubtasksAddAndDeleteViaManager_SubtaskAddedAndDeleted() {
        // given
        Subtask subtask = new Subtask(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, 2);
        Subtask subtask2 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, 2);
        Epic epic = new Epic(2, TaskStubs.TASK_NAME_2, TaskStubs.TASK_DESCRIPTION_2);
        // when
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask2);
        manager.deleteSubtask(subtask.getId());
        epic = manager.getEpic(epic.getId());
        // then
        assertEquals(1, epic.getSubtaskIds().size(), "must be only one id");
        assertEquals(3, epic.getSubtaskIds().getFirst(), "must be subtask with id 3");
    }

    @Test
    @DisplayName("Проверка наполнения менеджера задач")
    public void FillTaskManager_ManagerFilled() {
        // given
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        Subtask task3 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, 5);
        Subtask task4 = new Subtask(4, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, 5);
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewEpic(epic);
        manager.addNewSubtask(task3);
        manager.addNewSubtask(task4);
        // then
        assertEquals(2, manager.getTasks().size(), "Must be 2 tasks");
        assertEquals(2, manager.getSubtasks().size(), "Must be 2 subtasks");
        assertEquals(1, manager.getEpics().size(), "Must be 1 epic");
    }

    @Test
    @DisplayName("Проверка очистки менеджера задач")
    public void CleanTaskManager_ManagerCleared() {
        // given
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        Subtask task3 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, 5);
        Subtask task4 = new Subtask(4, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, 5);
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewEpic(epic);
        manager.addNewSubtask(task3);
        manager.addNewSubtask(task4);
        // then
        manager.deleteTasks();
        assertEquals(0, manager.getTasks().size(), "Must be 0 tasks");
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtasks().size(), "Must be 0 subtasks");
        manager.deleteEpics();
        assertEquals(0, manager.getEpics().size(), "Must be 0 epics");
    }
}
