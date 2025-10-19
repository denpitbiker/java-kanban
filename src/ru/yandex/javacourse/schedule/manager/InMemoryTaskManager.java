package ru.yandex.javacourse.schedule.manager;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.IN_PROGRESS;
import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import ru.yandex.javacourse.schedule.manager.comparator.TasksStartTimeComparator;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(new TasksStartTimeComparator());

    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values().stream().map(Task::clone).toList());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values().stream().map(Subtask::clone).toList());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values().stream().map(Epic::clone).toList());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        return new ArrayList<>(epic.getSubtaskIds().stream().map((id) -> subtasks.get(id).clone()).toList());
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.addTask(task);
        return task.clone();
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask.clone();
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic.clone();
    }

    @Override
    public Integer addNewTask(Task task) {
        if (task.getId() == null) {
            task.setId(getUniqueId());
        } else if (subtasks.containsKey(task.getId()) || epics.containsKey(task.getId())) {
            return null;
        }
        final int id = task.getId();
        final Task clonedTask = task.clone();
        if (hasTimeIntersection(clonedTask)) return null;
        tasks.put(id, clonedTask);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(clonedTask);
        }
        return id;
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        if (epic.getId() == null) {
            epic.setId(getUniqueId());
        } else if (tasks.containsKey(epic.getId()) || subtasks.containsKey(epic.getId())) {
            return null;
        }
        final int id = epic.getId();
        epics.put(id, epic.clone());
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        if (subtask.getId() == null) {
            subtask.setId(getUniqueId());
        } else if (tasks.containsKey(subtask.getId()) || epics.containsKey(subtask.getId())) {
            return null;
        }
        final int id = subtask.getId();

        final Subtask clonedSubtask = subtask.clone();
        if (hasTimeIntersection(clonedSubtask)) return null;
        subtasks.put(id, subtask.clone());
        if (clonedSubtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        epic.addSubtaskId(id);
        refreshEpicState(epicId);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        final Integer id = task.getId();
        if (id == null) return;
        final Task savedTask = tasks.get(id);
        if (savedTask == null) return;
        final Task clonedTask = task.clone();
        if (hasTimeIntersection(clonedTask)) return;
        if (savedTask.getStartTime() != null) {
            prioritizedTasks.remove(savedTask);
        }
        tasks.put(id, clonedTask);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(clonedTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        final Integer id = epic.getId();
        if (id == null) return;
        final Epic savedEpic = epics.get(id);
        if (savedEpic == null) return;
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        savedEpic.setStatus(epic.getStatus());
        if (epic.getStatus() == TaskStatus.DONE) {
            subtasks.values().forEach((subtask) -> {
                if (subtask.getEpicId() == id) {
                    subtask.setStatus(TaskStatus.DONE);
                }
            });
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Integer id = subtask.getId();
        if (id == null) return;
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) return;
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        final Subtask clonedSubtask = subtask.clone();
        if (hasTimeIntersection(clonedSubtask)) return;
        if (savedSubtask.getStartTime() != null) {
            prioritizedTasks.remove(savedSubtask);
        }
        subtasks.put(id, subtask.clone());
        if (clonedSubtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        refreshEpicState(epicId);
    }

    @Override
    public void deleteTask(int id) {
        Task removedTask = tasks.remove(id);
        if (removedTask == null) return;
        if (removedTask.getStartTime() != null) prioritizedTasks.remove(removedTask);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        final Epic epic = epics.remove(id);
        historyManager.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask removedSubtask = subtasks.remove(subtaskId);
            if (removedSubtask.getStartTime() != null) prioritizedTasks.remove(removedSubtask);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask == null) return;
        if (removedSubtask.getStartTime() != null) prioritizedTasks.remove(removedSubtask);
        historyManager.remove(id);
        Epic epic = epics.get(removedSubtask.getEpicId());
        epic.removeSubtask(id);
        refreshEpicState(epic.getId());
    }

    @Override
    public void deleteTasks() {
        for (Task t : tasks.values()) {
            historyManager.remove(t.getId());
            if (t.getStartTime() != null) prioritizedTasks.remove(t);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            refreshEpicState(epic.getId());
        }
        for (Task t : subtasks.values()) {
            historyManager.remove(t.getId());
            if (t.getStartTime() != null) prioritizedTasks.remove(t);
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Task t : epics.values()) {
            historyManager.remove(t.getId());
        }
        epics.clear();
        for (Task t : subtasks.values()) {
            historyManager.remove(t.getId());
            if (t.getStartTime() != null) prioritizedTasks.remove(t);
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory().stream().map(Task::clone).toList();
    }

    private boolean hasTimeIntersection(Task inputTask) {
        if (inputTask.getStartTime() == null) return false;
        return prioritizedTasks.stream().anyMatch(prioritizedTask -> {
                    if (inputTask.getEndTime() == null || prioritizedTask.getEndTime() == null) {
                        return inputTask.getStartTime().isEqual(prioritizedTask.getStartTime());
                    } else {
                        return inputTask.getEndTime().isAfter(prioritizedTask.getStartTime()) &&
                                inputTask.getStartTime().isBefore(prioritizedTask.getStartTime()) ||
                                prioritizedTask.getEndTime().isAfter(inputTask.getStartTime()) &&
                                        prioritizedTask.getStartTime().isBefore(inputTask.getStartTime());
                    }
                }
        );
    }

    private int getUniqueId() {
        int id = 1;
        while (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id)) {
            id++;
        }
        return id;
    }

    private void refreshEpicState(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(NEW);
            return;
        }
        TaskStatus status = null;
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        Duration epicDuration = null;
        for (int id : epic.getSubtaskIds()) {
            final Subtask subtask = subtasks.get(id);
            if (epicStartTime == null || subtask.getStartTime() != null && epicStartTime.isAfter(subtask.getStartTime())) {
                epicStartTime = subtask.getStartTime();
            }
            if (epicEndTime == null || subtask.getEndTime() != null && epicEndTime.isBefore(subtask.getEndTime())) {
                epicEndTime = subtask.getEndTime();
            }
            if (subtask.getDuration() != null) {
                if (epicDuration == null) {
                    epicDuration = subtask.getDuration();
                } else {
                    epicDuration = epicDuration.plus(subtask.getDuration());
                }
            }
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            if (status == subtask.getStatus() && status != IN_PROGRESS) {
                continue;
            }
            epic.setStatus(IN_PROGRESS);
            return;
        }
        epic.setStatus(status);
        epic.setStartTime(epicStartTime);
        epic.setDuration(epicDuration);
        epic.setEndTime(epicEndTime);
    }
}
