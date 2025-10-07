package ru.yandex.javacourse.schedule.manager;

import java.util.List;

import ru.yandex.javacourse.schedule.tasks.Task;

/**
 * In memory history manager.
 *
 * @author Vladimir Ivanov (ivanov.vladimir.l@gmail.com)
 */
public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedIdDataStorage<Task, Integer> history = new LinkedIdDataStorage<>();

    @Override
    public List<Task> getHistory() {
        return history.getIndexedItems();
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        history.add(task, task.getId());
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}
