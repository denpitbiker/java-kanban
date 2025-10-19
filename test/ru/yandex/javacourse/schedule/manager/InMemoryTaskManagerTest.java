package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private final Task dateTimeTask = new Task(
            1,
            TaskStubs.TASK_NAME_1,
            TaskStubs.TASK_DESCRIPTION_1,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30
    );

    private final Task intersectingTask = new Task(
            2,
            TaskStubs.TASK_NAME_2,
            TaskStubs.TASK_DESCRIPTION_2,
            TaskStatus.NEW,
            TaskStubs.DURATION_1D,
            TaskStubs.DATE_TIME_19_10_2025_12_30.plusHours(1)
    );

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

    private final Epic epic = new Epic(
            3,
            TaskStubs.TASK_NAME_3,
            TaskStubs.TASK_DESCRIPTION_3
    );

    @Override
    @BeforeEach
    public void initManager() {
        //given
        manager = Managers.getInMemoryTaskManager();
    }

    @Test
    @DisplayName("Проверка защиты от добавления пересекающейся по времени задачи (пересекающаяся начинается позже)")
    public void addTask_IntersectingAfterTasksAdding_IntersectingTaskNotAdded() {
        // when
        manager.addNewTask(dateTimeTask);
        manager.addNewTask(intersectingTask);
        // then
        assertEquals(1, manager.getTasks().size(), "Must be 1 task");
        assertEquals(1, manager.getTasks().getFirst().getId(), "Must be task with id 1");
    }

    @Test
    @DisplayName("Проверка защиты от добавления пересекающейся по времени задачи (пересекающаяся начинается раньше)")
    public void addTask_IntersectingBeforeTasksAdding_IntersectingTaskNotAdded() {
        // when
        manager.addNewTask(dateTimeTask);
        manager.addNewTask(intersectingTask);
        // then
        assertEquals(1, manager.getTasks().size(), "Must be 1 task");
        assertEquals(1, manager.getTasks().getFirst().getId(), "Must be task with id 1");
    }

    @Test
    @DisplayName("Проверка добавления задач, не пересекающихся по времени")
    public void addTask_NotIntersectingTasksAdding_TasksAdded() {
        // given
        Task notIntersectingTask = new Task(
                2,
                TaskStubs.TASK_NAME_1,
                TaskStubs.TASK_DESCRIPTION_1,
                TaskStatus.NEW,
                TaskStubs.DURATION_1D,
                TaskStubs.DATE_TIME_19_10_2025_12_30.plusDays(1)
        );
        // when
        manager.addNewTask(dateTimeTask);
        manager.addNewTask(notIntersectingTask);
        // then
        assertEquals(2, manager.getTasks().size(), "Must be 2 tasks");
    }

    @Test
    @DisplayName("Проверка защиты от добавления пересекающейся по времени подзадачи (пересекающаяся начинается позже)")
    public void addSubtask_IntersectingAfterSubtasksAdding_IntersectingSubtaskNotAdded() {
        // given
        manager.addNewEpic(epic);
        // when
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewSubtask(intersectingSubtask);
        // then
        assertEquals(1, manager.getSubtasks().size(), "Must be 1 task");
        assertEquals(1, manager.getSubtasks().getFirst().getId(), "Must be task with id 1");
    }

    @Test
    @DisplayName("Проверка защиты от добавления пересекающейся по времени подзадачи (пересекающаяся начинается раньше)")
    public void addSubtask_IntersectingBeforeSubtasksAdding_IntersectingSubtaskNotAdded() {
        // given
        manager.addNewEpic(epic);
        // when
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewSubtask(intersectingSubtask);
        // then
        assertEquals(1, manager.getSubtasks().size(), "Must be 1 task");
        assertEquals(1, manager.getSubtasks().getFirst().getId(), "Must be task with id 1");
    }

    @Test
    @DisplayName("Проверка добавления подзадач, не пересекающихся по времени")
    public void addSubtask_NotIntersectingSubtasksAdding_SubtasksAdded() {
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
        manager.addNewEpic(epic);
        // when
        manager.addNewSubtask(dateTimeSubtask);
        manager.addNewSubtask(notIntersectingSubtask);
        // then
        assertEquals(2, manager.getSubtasks().size(), "Must be 2 tasks");
    }

    @Test
    @DisplayName("Проверка получения списка задач, осортированных по приоритету (времени)")
    public void getPrioritizedTasks_GetPrioritizedTasks_PrioritizedTasksList() {
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
        // when
        manager.addNewTask(notIntersectingTask);
        manager.addNewSubtask(notIntersectingSubtask);
        manager.addNewSubtask(dateTimeSubtask);
        // then
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size(), "Must be 3 tasks");
        assertEquals(1, prioritizedTasks.getFirst().getId(), "Must be subtask with id = 1");
        assertEquals(4, prioritizedTasks.getLast().getId(), "Must be task with id = 4");
    }
}
