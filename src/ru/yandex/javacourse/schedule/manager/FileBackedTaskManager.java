package ru.yandex.javacourse.schedule.manager;

import ru.yandex.javacourse.schedule.manager.exception.ManagerLoadException;
import ru.yandex.javacourse.schedule.manager.exception.ManagerSaveException;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksFile;

    private FileBackedTaskManager(File file) {
        tasksFile = file;
        load();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    @Override
    public Integer addNewTask(Task task) {
        Integer id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public Integer addNewEpic(Epic epic) {
        Integer id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Integer id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    private void load() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(tasksFile))) {
            List<Subtask> parsedSubtasks = new LinkedList<>();
            fileReader.lines().forEach((line) ->
            {
                Task parsedTask = parseTask(line);
                if (parsedTask instanceof Epic) {
                    addNewEpic((Epic) parsedTask);
                } else if (parsedTask instanceof Subtask) {
                    parsedSubtasks.add((Subtask) parsedTask);
                } else {
                    addNewTask(parsedTask);
                }
            });
            parsedSubtasks.forEach(this::addNewSubtask);
        } catch (IOException e) {
            throw new ManagerLoadException("Failed to load data data from file: " + tasksFile.getName());
        } catch (TaskParsingException e) {
            throw new ManagerLoadException(e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter fileWriter = Files.newBufferedWriter(tasksFile.toPath(), StandardOpenOption.TRUNCATE_EXISTING)) {
            StringBuilder sb = new StringBuilder();
            tasks.values().forEach((task) -> {
                sb.append(serializeTask(task));
                sb.append('\n');
            });
            epics.values().forEach((task) -> {
                sb.append(serializeTask(task));
                sb.append('\n');
            });
            subtasks.values().forEach((task) -> {
                sb.append(serializeTask(task));
                sb.append('\n');
            });
            if (!sb.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
            fileWriter.append(sb);
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save data to file: " + tasksFile.getName());
        }
    }

    private String serializeTask(Task task) {
        StringBuilder sb = new StringBuilder();
        if (task instanceof Epic) {
            sb.append('E');
        } else if (task instanceof Subtask) {
            sb.append('S');
        } else {
            sb.append('T');
        }
        sb.append(SEPARATOR);
        sb.append(task.getId());
        sb.append(SEPARATOR);
        sb.append(task.getName());
        sb.append(SEPARATOR);
        sb.append(serializeTaskStatus(task.getStatus()));
        sb.append(SEPARATOR);
        sb.append(task.getDescription());
        sb.append(SEPARATOR);
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        }
        return sb.toString();
    }

    private Task parseTask(String serializedTask) {
        String[] taskParts = serializedTask.split(Character.toString(SEPARATOR));
        char type;
        String name;
        Integer id;
        TaskStatus status;
        String description;
        String epicId;
        Task task;

        try {
            type = parseTaskPartOrThrow(taskParts, TASK_TYPE_PART_NAME, 0).charAt(0);
        } catch (StringIndexOutOfBoundsException e) {
            throw new TaskParsingException(TASK_TYPE_PART_NAME, serializedTask);
        }

        try {
            String parsedIdString = parseTaskPartOrThrow(taskParts, TASK_ID_PART_NAME, 1);
            if (parsedIdString.equals(NULL_STRING)) {
                id = null;
            } else {
                id = Integer.parseInt(parsedIdString);
            }
        } catch (NumberFormatException e) {
            throw new TaskParsingException(TASK_ID_PART_NAME, serializedTask);
        }

        try {
            status = deserializeTaskStatus(parseTaskPartOrThrow(taskParts, TASK_STATUS_PART_NAME, 3).charAt(0));
        } catch (StringIndexOutOfBoundsException | IllegalStateException e) {
            throw new TaskParsingException(TASK_STATUS_PART_NAME, serializedTask);
        }

        name = parseTaskPartOrThrow(taskParts, TASK_NAME_PART_NAME, 2);
        description = parseTaskPartOrThrow(taskParts, TASK_DESCRIPTION_PART_NAME, 4);

        if (type == 'E') {
            task = new Epic(name, description);
            task.setStatus(status);
        } else if (type == 'S') {
            epicId = parseTaskPartOrThrow(taskParts, TASK_EPIC_ID_PART_NAME, 5);
            task = new Subtask(name, description, status, Integer.parseInt(epicId));
        } else {
            task = new Task(name, description, status);
        }
        if (id != null) {
            task.setId(id);
        }
        return task;
    }

    private String parseTaskPartOrThrow(String[] taskParts, String partName, int partIndex) {
        try {
            return taskParts[partIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new TaskParsingException(partName, String.join(String.valueOf(SEPARATOR), taskParts));
        }
    }

    private char serializeTaskStatus(TaskStatus status) {
        return switch (status) {
            case NEW -> 'N';
            case IN_PROGRESS -> 'P';
            case DONE -> 'D';
        };
    }

    private TaskStatus deserializeTaskStatus(char status) {
        return switch (status) {
            case 'N' -> TaskStatus.NEW;
            case 'P' -> TaskStatus.IN_PROGRESS;
            case 'D' -> TaskStatus.DONE;
            default -> throw new TaskStatusParsingException(status);
        };
    }

    private static class TaskStatusParsingException extends RuntimeException {
        public TaskStatusParsingException(char status) {
            super("Failed to parse task status: " + status);
        }
    }

    private static class TaskParsingException extends RuntimeException {
        public TaskParsingException(String taskPart, String line) {
            super("Failed to parse task part: " + taskPart + ". Error line: " + line);
        }
    }

    private final static char SEPARATOR = ',';
    private final static String NULL_STRING = "null";

    private final static String TASK_TYPE_PART_NAME = "task type (class)";
    private final static String TASK_ID_PART_NAME = "id";
    private final static String TASK_NAME_PART_NAME = "name";
    private final static String TASK_DESCRIPTION_PART_NAME = "description";
    private final static String TASK_STATUS_PART_NAME = "status";
    private final static String TASK_EPIC_ID_PART_NAME = "epicId";
}
