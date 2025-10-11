package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.tasks.exception.SameIdException;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    private static final String EXPECTED_MESSAGE = "epicId can't be the same with subtask id!";

    @Test
    @DisplayName("Проверка, что подзадачи сравниваются по id")
    public void equals_SubtaskComparison_Equal() {
        // given
        Subtask subtask = new Subtask(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, 2);
        // when
        Subtask sameIdSubtask = new Subtask(1, TaskStubs.TASK_NAME_2, TaskStubs.TASK_DESCRIPTION_2, TaskStatus.IN_PROGRESS, 2);
        // then
        assertEquals(subtask, sameIdSubtask, "subtask entities should be compared by id");
    }

    @Test
    @DisplayName("Проверка, что при попытке создать подзадачу с тем же id, что и у эпика, бросится исключение ")
    public void constructor_CreateSubtaskSameIdAndEpicId_ThrowSameIdException() {
        // given
        Exception exception = assertThrows(
                SameIdException.class,
                () -> new Subtask(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, 1)
        );
        // when
        String actualMessage = exception.getMessage();
        // then
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }
}
