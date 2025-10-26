package ru.yandex.javacourse.schedule.network.gson.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) jsonWriter.value(NULL_STRING);
        else jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String serializedDuration = jsonReader.nextString();
        if (serializedDuration.equals(NULL_STRING)) return null;
        return Duration.parse(serializedDuration);
    }

    private static final String NULL_STRING = "null";
}
