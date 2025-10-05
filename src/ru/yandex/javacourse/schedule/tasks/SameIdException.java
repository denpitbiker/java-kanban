package ru.yandex.javacourse.schedule.tasks;

public class SameIdException extends RuntimeException {
    public SameIdException(String message) {
        super(message);
    }
}
