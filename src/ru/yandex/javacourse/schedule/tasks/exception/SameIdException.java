package ru.yandex.javacourse.schedule.tasks.exception;

public class SameIdException extends RuntimeException {
    public SameIdException(String message) {
        super(message);
    }
}
