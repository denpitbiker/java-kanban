package ru.yandex.javacourse.schedule.network;

public enum NetworkRequestMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE");

    private NetworkRequestMethod(String name) {
        this.name = name;
    }

    public final String name;
}
