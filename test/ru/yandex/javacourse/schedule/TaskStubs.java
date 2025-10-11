package ru.yandex.javacourse.schedule;

import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

public class TaskStubs {
    public static final String TASK_NAME_1 = "Test 1";
    public static final String TASK_NAME_2 = "Test 2";
    public static final String TASK_NAME_3 = "Test 3";
    public static final String TASK_DESCRIPTION_1 = "Description 1";
    public static final String TASK_DESCRIPTION_2 = "Description 2";
    public static final String TASK_DESCRIPTION_3 = "Description 3";
    // Should not be modified in tests
    public static Task TASK_STUB_1 = new Task(1, TASK_NAME_1, TASK_DESCRIPTION_1, TaskStatus.NEW);
    public static Task TASK_STUB_2 = new Task(2, TASK_NAME_2, TASK_DESCRIPTION_2, TaskStatus.NEW);
    public static Task TASK_STUB_3 = new Task(3, TASK_NAME_3, TASK_DESCRIPTION_3, TaskStatus.NEW);
}
