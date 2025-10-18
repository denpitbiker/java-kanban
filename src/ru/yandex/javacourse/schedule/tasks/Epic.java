package ru.yandex.javacourse.schedule.tasks;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task implements Cloneable {
	protected ArrayList<Integer> subtaskIds = new ArrayList<>();
	protected LocalDateTime endTime;

	public Epic(int id, String name, String description) {
		super(id, name, description, NEW, null, null);
	}

	public Epic(String name, String description) {
		super(name, description, NEW, null, null);
	}

	private Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds) {
		super(id, name, description, status, null, null);
		subtaskIds.forEach(this::addSubtaskId);
	}

	private Epic(String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds) {
		super(name, description, status, null, null);
		subtaskIds.forEach(this::addSubtaskId);
	}

	public void addSubtaskId(int id) {
		if (subtaskIds.contains(id) || this.id == id) return;
		subtaskIds.add(id);
	}

	public List<Integer> getSubtaskIds() {
		return subtaskIds;
	}

	@Override
	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public void cleanSubtaskIds() {
		subtaskIds.clear();
	}

	public void removeSubtask(int id) {
		subtaskIds.remove(Integer.valueOf(id));
	}

	@Override
	public String toString() {
		return "Epic{" +
				"id=" + id +
				", name='" + name + '\'' +
				", status=" + status +
				", description='" + description + '\'' +
				", subtaskIds=" + subtaskIds +
				'}';
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public Epic clone() {
		Epic clone;
        if (id == null) {
			clone = new Epic(name, description, status, subtaskIds);
		} else {
			clone = new Epic(id, name, description, status, subtaskIds);
		}
		clone.setDuration(duration);
		clone.setStartTime(startTime);
		return clone;
	}
}
