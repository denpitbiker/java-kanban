package ru.yandex.javacourse.schedule.network;

public enum NetworkCode {
    SUCCESS(200),
    SUCCESS_CREATE(201),
    NOT_FOUND(404),
    NOT_ACCEPTABLE(406),
    INTERNAL_SERVER_ERROR(500);

    private NetworkCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public final int statusCode;
}
