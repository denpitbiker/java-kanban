package ru.yandex.javacourse.schedule.manager.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Integer taskId) {
        super(MESSAGE + taskId);
    }

    private static final String MESSAGE = "Can't find task with id: ";
}
