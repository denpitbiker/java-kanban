package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    @DisplayName("Проверка, что задачи сравниваются по id")
    public void equals_TaskComparison_Equal() {
        // given
        Task task = new Task(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, null, null);
        // when
        Task sameIdTask = new Task(1, TaskStubs.TASK_NAME_2, TaskStubs.TASK_DESCRIPTION_2, TaskStatus.IN_PROGRESS, null, null);
        // then
        assertEquals(task, sameIdTask, "task entities should be compared by id");
    }

    @Test
    @DisplayName("Проверка рассчета времени завершения задачи")
    public void getEndTime_TaskEndTimeCalculation_CorrectEndTime() {
        // given
        Task task = new Task(
                1,
                TaskStubs.TASK_NAME_1,
                TaskStubs.TASK_DESCRIPTION_1,
                TaskStatus.NEW,
                TaskStubs.DURATION_1D,
                TaskStubs.DATE_TIME_19_10_2025_12_30);
        LocalDateTime expectedEndTime = LocalDateTime.of(2025, 10, 20, 12, 30);
        // when
        LocalDateTime actualEndTime = task.getEndTime();
        // then
        assertEquals(expectedEndTime, actualEndTime, "endDateTime should be 2025.10.20 12:30");
    }
}
