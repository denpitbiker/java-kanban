package ru.yandex.javacourse.schedule.network.gson.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) jsonWriter.value(NULL_STRING);
        else jsonWriter.value(localDateTime.toString());
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String serializedLocalDateTime = jsonReader.nextString();
        if (serializedLocalDateTime.equals(NULL_STRING)) return null;
        return LocalDateTime.parse(serializedLocalDateTime);
    }

    private static final String NULL_STRING = "null";
}
