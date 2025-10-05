package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void initHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void testHistoricVersions() {
        Task task = new Task(1, "Test 1", "Testing task 1", TaskStatus.NEW);
        historyManager.addTask(task);
        assertEquals(1, historyManager.getHistory().size(), "historic task should be added");
        Task task2 = new Task(2, "Test 2", "Testing task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        assertEquals(2, historyManager.getHistory().size(), "historic task should be added");
    }

    @Test
    public void testHistoricVersionsByPointer() {
        Task task = new Task(1, "Test 1", "Testing task 1", TaskStatus.NEW);
        historyManager.addTask(task);
        assertEquals(task.getStatus(), historyManager.getHistory().getFirst().getStatus(), "historic task should be stored");
        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.addTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().getFirst().getStatus(), "historic task should be changed");
        assertEquals(1, historyManager.getHistory().size(), "no task duplicate should exist");
    }

    @Test
    public void testAddHistory() {
        Task task = new Task(1, "Test 1", "Testing task 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Test 2", "Testing task 2", TaskStatus.NEW);
        Task task3 = new Task(3, "Test 3", "Testing task 3", TaskStatus.NEW);
        historyManager.addTask(task);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "history size should be equals 3");
        assertEquals(2, historyManager.getHistory().get(1).getId(), "id should be  equal 2");
        historyManager.addTask(task2);
        assertEquals(3, historyManager.getHistory().get(1).getId(), "id should be  equal 3");
    }

    @Test
    public void testRemoveHistory() {
        Task task = new Task(1, "Test 1", "Testing task 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Test 2", "Testing task 2", TaskStatus.NEW);
        Task task3 = new Task(3, "Test 3", "Testing task 3", TaskStatus.NEW);
        historyManager.addTask(task);
        historyManager.addTask(task2);
        historyManager.addTask(task3);
        historyManager.remove(task2.getId());
        assertEquals(2, historyManager.getHistory().size(), "history size should be equals 2");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "id should be  equal 2");
        historyManager.remove(task.getId());
        assertEquals(1, historyManager.getHistory().size(), "history size should be equals 1");
        assertEquals(3, historyManager.getHistory().getFirst().getId(), "id should be  equal 3");
        historyManager.remove(task3.getId());
        assertEquals(0, historyManager.getHistory().size(), "history size should be equals 0");
        historyManager.remove(task3.getId());
        assertEquals(0, historyManager.getHistory().size(), "history size should be equals 0");
    }

}
