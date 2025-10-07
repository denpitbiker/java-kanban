package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    @Test
    @DisplayName("Проверка, что эпики сравниваются по id")
    public void equals_EpicComparison_Equal() {
        // given
        Epic epic1 = new Epic(1, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        Epic epic2 = new Epic(1, TaskStubs.TASK_NAME_2, TaskStubs.TASK_DESCRIPTION_2);
        // then
        assertEquals(epic1, epic2, "task and subentities should be compared by id");
    }

    @Test
    @DisplayName("Проверка, что к эпику нельзя добавить подзадачу только с уникальным (для эпика) id")
    public void addSubtaskId_OnlyUniqueSubtasks_TwoUniqueSubtasksAdded() {
        // given
        Epic epic = new Epic(0, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        epic.addSubtaskId(1);
        epic.addSubtaskId(2);
        epic.addSubtaskId(1);
        // then
        assertEquals(2, epic.subtaskIds.size(), "should not add same subtask id twice");
    }

    @Test
    @DisplayName("Проверка, что эпик нельзя сделать подзадачей самого себя")
    public void addSubtaskId_LinkEpicToItself_SubtaskNotAdded() {
        // given
        Epic epic = new Epic(0, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        epic.addSubtaskId(0);
        // then
        assertEquals(0, epic.subtaskIds.size(), "epic should not add itself as subtask");
    }
}
