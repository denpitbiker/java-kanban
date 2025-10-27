package ru.yandex.javacourse.schedule.network;

public enum NetworkHeader {
    CONTENT_TYPE("Content-Type");

    private NetworkHeader(String name) {
        this.name = name;
    }

    public final String name;
}
