package ru.yandex.javacourse.schedule.tasks;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task implements Cloneable {
	protected ArrayList<Integer> subtaskIds = new ArrayList<>();

	public Epic(int id, String name, String description) {
		super(id, name, description, NEW);
	}

	public Epic(String name, String description) {
		super(name, description, NEW);
	}

	private Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds) {
		super(id, name, description, status);
		subtaskIds.forEach(this::addSubtaskId);
	}

	private Epic(String name, String description, TaskStatus status, ArrayList<Integer> subtaskIds) {
		super(name, description, status);
		subtaskIds.forEach(this::addSubtaskId);
	}

	public void addSubtaskId(int id) {
		if (subtaskIds.contains(id) || this.id == id) return;
		subtaskIds.add(id);
	}

	public List<Integer> getSubtaskIds() {
		return subtaskIds;
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
        if (id == null) {
			return new Epic(name, description, status, subtaskIds);
		} else {
			return new Epic(id, name, description, status, subtaskIds);
		}
	}
}
