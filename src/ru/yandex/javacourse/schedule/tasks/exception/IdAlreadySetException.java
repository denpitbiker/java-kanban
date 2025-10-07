package ru.yandex.javacourse.schedule.tasks.exception;

public class IdAlreadySetException extends RuntimeException {
    public IdAlreadySetException(int id) {
        super("Id is already set (id=" + id + ")!");
    }
}
