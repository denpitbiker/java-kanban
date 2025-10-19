package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    final Subtask subtaskWithId3 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, null, null, 5);
    final Subtask subtaskWithId4 = new Subtask(4, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null, 5);

    T manager;

    public abstract void initManager();

    @Test
    @DisplayName("Проверка получения задачи по id из менеджера задач")
    public void getTask_GetTaskById_TaskFound() {
        // given
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null);
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
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null);
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
        Task task1before = new Task(id, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, status, null, null);
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
        Subtask subtask = new Subtask(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null, 2);
        Subtask subtask2 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, null, null, 2);
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
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null);
        Subtask task3 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, null, null, 5);
        Subtask task4 = new Subtask(4, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null, 5);
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
    @DisplayName("Проверка, что подзадача не добавится до создания эпика")
    public void addSubtask_AddSubtaskBeforeEpic_SubtaskNotAdded() {
        // given
        Subtask task3 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, null, null, 5);
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        manager.addNewSubtask(task3);
        manager.addNewEpic(epic);
        // then
        assertEquals(0, manager.getSubtasks().size(), "Must be 0 subtasks");
    }

    @Test
    @DisplayName("Проверка очистки менеджера задач")
    public void CleanTaskManager_ManagerCleared() {
        // given
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null);
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtaskWithId3);
        manager.addNewSubtask(subtaskWithId4);
        // then
        manager.deleteTasks();
        assertEquals(0, manager.getTasks().size(), "Must be 0 tasks");
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtasks().size(), "Must be 0 subtasks");
        manager.deleteEpics();
        assertEquals(0, manager.getEpics().size(), "Must be 0 epics");
    }

    @Test
    @DisplayName("Проверка статуса эпика (все подзадачи со статусом NEW)")
    public void getStatus_GetEpicStatusAllSubtasksNew_EpicStatusNew() {
        // given
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtaskWithId3);
        manager.addNewSubtask(subtaskWithId4);
        // then
        assertEquals(TaskStatus.NEW, manager.getEpics().getFirst().getStatus(), "Epic status must be NEW");
    }

    @Test
    @DisplayName("Проверка статуса эпика (все подзадачи со статусом DONE)")
    public void getStatus_GetEpicStatusAllSubtasksDone_EpicStatusDone() {
        // given
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        Subtask subtaskWithId3Done = subtaskWithId3.clone();
        subtaskWithId3Done.setStatus(TaskStatus.DONE);
        Subtask subtaskWithId4Done = subtaskWithId4.clone();
        subtaskWithId4Done.setStatus(TaskStatus.DONE);
        // when
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtaskWithId3Done);
        manager.addNewSubtask(subtaskWithId4Done);
        // then
        assertEquals(TaskStatus.DONE, manager.getEpics().getFirst().getStatus(), "Epic status must be DONE");
    }

    @Test
    @DisplayName("Проверка статуса эпика (все подзадачи со статусом IN_PROGRESS)")
    public void getStatus_GetEpicStatusAllSubtasksInProgress_EpicStatusInProgress() {
        // given
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        Subtask subtaskWithId3InProgress = subtaskWithId3.clone();
        subtaskWithId3InProgress.setStatus(TaskStatus.IN_PROGRESS);
        Subtask subtaskWithId4InProgress = subtaskWithId4.clone();
        subtaskWithId4InProgress.setStatus(TaskStatus.IN_PROGRESS);
        // when
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtaskWithId3InProgress);
        manager.addNewSubtask(subtaskWithId4InProgress);
        // then
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpics().getFirst().getStatus(), "Epic status must be IN_PROGRESS");
    }

    @Test
    @DisplayName("Проверка статуса эпика (все подзадачи со статусом NEW или DONE)")
    public void getStatus_GetEpicStatusSubtasksNewOrDone_EpicStatusInProgress() {
        // given
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        Subtask subtaskWithId4Done = subtaskWithId4.clone();
        subtaskWithId4Done.setStatus(TaskStatus.DONE);
        // when
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtaskWithId3);
        manager.addNewSubtask(subtaskWithId4Done);
        // then
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpics().getFirst().getStatus(), "Epic status must be IN_PROGRESS");
    }

    @Test
    @DisplayName("Проверка рассчета времени окончания эпика")
    public void getEndTime_GetEpicEndTime_EpicStatusInProgress() {
        // given
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        Subtask subtaskWithId3AndTimings = subtaskWithId3.clone();
        subtaskWithId3AndTimings.setStartTime(TaskStubs.DATE_TIME_19_10_2025_12_30);
        subtaskWithId3AndTimings.setDuration(Duration.ofDays(10));
        Subtask subtaskWithId4AndTimings = subtaskWithId4.clone();
        subtaskWithId4AndTimings.setStartTime(TaskStubs.DATE_TIME_20_10_2025_12_30);
        LocalDateTime expectedEndDateTime = LocalDateTime.of(2025,10,29, 12, 30);
        // when
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtaskWithId3AndTimings);
        manager.addNewSubtask(subtaskWithId4AndTimings);
        // then
        assertEquals(expectedEndDateTime, manager.getEpics().getFirst().getEndTime(), "Epic endTime must be 29.10.2025 12:30");
    }
}
