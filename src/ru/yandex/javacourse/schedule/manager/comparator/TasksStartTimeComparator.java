package ru.yandex.javacourse.schedule.manager.comparator;

import ru.yandex.javacourse.schedule.tasks.Task;

import java.util.Comparator;

public class TasksStartTimeComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        return t1.getStartTime().compareTo(t2.getStartTime());
    }
}
