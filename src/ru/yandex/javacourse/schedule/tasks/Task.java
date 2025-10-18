package ru.yandex.javacourse.schedule.tasks;

import ru.yandex.javacourse.schedule.tasks.exception.IdAlreadySetException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Cloneable {
    protected Integer id;
    protected String name;
    protected TaskStatus status;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(
            int id,
            String name,
            String description,
            TaskStatus status,
            Duration duration,
            LocalDateTime startTime
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) throws IdAlreadySetException {
        if (this.id != null) throw new IdAlreadySetException(this.id);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public Task clone() {
        try {
            return (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            if (id == null) {
                return new Task(name, description, status, duration, startTime);
            } else {
                return new Task(id, name, description, status, duration, startTime);
            }
        }
    }
}
