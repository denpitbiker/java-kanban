package ru.yandex.javacourse.schedule.tasks;

import ru.yandex.javacourse.schedule.tasks.exception.SameIdException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task implements Cloneable {
    protected int epicId;

    public Subtask(
            int id,
            String name,
            String description,
            TaskStatus status,
            Duration duration,
            LocalDateTime startTime,
            int epicId) throws SameIdException {
        super(id, name, description, status, duration, startTime);
        if (id == epicId) throw new SameIdException("epicId can't be the same with subtask id!");
        this.epicId = epicId;
    }

    public Subtask(
            String name,
            String description,
            TaskStatus status,
            Duration duration,
            LocalDateTime startTime,
            int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Subtask clone() {
        if (id == null) {
            return new Subtask(name, description, status, duration, startTime, epicId);
        } else {
            return new Subtask(id, name, description, status, duration, startTime, epicId);
        }
    }
}
