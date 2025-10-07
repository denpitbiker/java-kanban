package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    @DisplayName("Проверка существования стандартной реализации менеджера истории задач")
    public void getDefaultHistoryManager_GetDefaultHistoryManager_ReturnHistoryManagerManager() {
        assertNotNull(Managers.getDefaultHistoryManager(), "default history managers should not be null");
    }

    @Test
    @DisplayName("Проверка существования стандартной реализации менеджера задач")
    public void getDefaultTaskManager_GetDefaultTaskManager_ReturnTaskManager() {
        assertNotNull(Managers.getDefaultTaskManager(), "default manager should not be null");
    }
}
