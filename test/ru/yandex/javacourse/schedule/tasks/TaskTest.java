package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    @DisplayName("Проверка, что задачи сравниваются по id")
    public void equals_TaskComparison_Equal() {
        // given
        Task task = new Task(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        // when
        Task sameIdTask = new Task(1, TaskStubs.TASK_NAME_2, TaskStubs.TASK_DESCRIPTION_2, TaskStatus.IN_PROGRESS);
        // then
        assertEquals(task, sameIdTask, "task entities should be compared by id");
    }
}
