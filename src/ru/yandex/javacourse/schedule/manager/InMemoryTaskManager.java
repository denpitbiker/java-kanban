package ru.yandex.javacourse.schedule.manager;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.IN_PROGRESS;
import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

	private final Map<Integer, Task> tasks = new HashMap<>();
	private final Map<Integer, Epic> epics = new HashMap<>();
	private final Map<Integer, Subtask> subtasks = new HashMap<>();
	private final HistoryManager historyManager = Managers.getDefaultHistory();


	@Override
	public ArrayList<Task> getTasks() {
		return new ArrayList<>(this.tasks.values());
	}

	@Override
	public ArrayList<Subtask> getSubtasks() {
		return new ArrayList<>(subtasks.values());
	}

	@Override
	public ArrayList<Epic> getEpics() {
		return new ArrayList<>(epics.values());
	}

	@Override
	public ArrayList<Subtask> getEpicSubtasks(int epicId) {
		ArrayList<Subtask> tasks = new ArrayList<>();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			return null;
		}
		for (int id : epic.getSubtaskIds()) {
			tasks.add(subtasks.get(id));
		}
		return tasks;
	}

	@Override
	public Task getTask(int id) {
		final Task task = tasks.get(id);
		historyManager.addTask(task);
		return task;
	}

	@Override
	public Subtask getSubtask(int id) {
		final Subtask subtask = subtasks.get(id);
		historyManager.addTask(subtask);
		return subtask;
	}

	@Override
	public Epic getEpic(int id) {
		final Epic epic = epics.get(id);
		historyManager.addTask(epic);
		return epic;
	}

	@Override
	public Integer addNewTask(Task task) {
		if (task.getId() == null) {
			task.setId(getUniqueId());
		} else if (subtasks.containsKey(task.getId()) || epics.containsKey(task.getId())) {
			return null;
		}
		final int id = task.getId();
		tasks.put(id, task);
		return id;
	}

	@Override
	public Integer addNewEpic(Epic epic) {
		if (epic.getId() == null) {
			epic.setId(getUniqueId());
		} else if (tasks.containsKey(epic.getId()) || subtasks.containsKey(epic.getId())) {
			return null;
		}
		final int id = epic.getId();
		epics.put(id, epic);
		return id;
	}

	@Override
	public Integer addNewSubtask(Subtask subtask) {
		final int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			return null;
		}
		if (subtask.getId() == null) {
			subtask.setId(getUniqueId());
		} else if (tasks.containsKey(subtask.getId()) || epics.containsKey(subtask.getId())) {
			return null;
		}
		final int id = subtask.getId();
		subtasks.put(id, subtask);
		epic.addSubtaskId(id);
		updateEpicStatus(epicId);
		return id;
	}

	@Override
	public void updateTask(Task task) {
		final Integer id = task.getId();
		if (id == null) return;
		final Task savedTask = tasks.get(id);
		if (savedTask == null) return;
		tasks.put(id, task);
	}

	@Override
	public void updateEpic(Epic epic) {
		final Integer id = epic.getId();
		if (id == null) return;
		final Epic savedEpic = epics.get(id);
		if (savedEpic == null) return;
		savedEpic.setName(epic.getName());
		savedEpic.setDescription(epic.getDescription());
		savedEpic.setStatus(epic.getStatus());
        if (epic.getStatus() == TaskStatus.DONE) {
            subtasks.values().forEach((subtask) -> {
                if (subtask.getEpicId() == id) {
                    subtask.setStatus(TaskStatus.DONE);
                }
            });
        }
	}

	@Override
	public void updateSubtask(Subtask subtask) {
		final Integer id = subtask.getId();
		if (id == null) return;
		final int epicId = subtask.getEpicId();
		final Subtask savedSubtask = subtasks.get(id);
		if (savedSubtask == null) return;
		final Epic epic = epics.get(epicId);
		if (epic == null) {
			return;
		}
		subtasks.put(id, subtask);
		updateEpicStatus(epicId);
	}

	@Override
	public void deleteTask(int id) {
		tasks.remove(id);
		historyManager.remove(id);
	}

	@Override
	public void deleteEpic(int id) {
		final Epic epic = epics.remove(id);
		historyManager.remove(id);
		for (Integer subtaskId : epic.getSubtaskIds()) {
			subtasks.remove(subtaskId);
			historyManager.remove(subtaskId);
		}
	}

	@Override
	public void deleteSubtask(int id) {
		Subtask subtask = subtasks.remove(id);
		if (subtask == null) return;
		historyManager.remove(id);
		Epic epic = epics.get(subtask.getEpicId());
		epic.removeSubtask(id);
		updateEpicStatus(epic.getId());
	}

	@Override
	public void deleteTasks() {
		for (Task t : tasks.values()) {
			historyManager.remove(t.getId());
		}
		tasks.clear();
	}

	@Override
	public void deleteSubtasks() {
		for (Epic epic : epics.values()) {
			epic.cleanSubtaskIds();
			updateEpicStatus(epic.getId());
		}
		for (Task t : subtasks.values()) {
			historyManager.remove(t.getId());
		}
		subtasks.clear();
	}

	@Override
	public void deleteEpics() {
		for (Task t : epics.values()) {
			historyManager.remove(t.getId());
		}
		epics.clear();
		for (Task t : subtasks.values()) {
			historyManager.remove(t.getId());
		}
		subtasks.clear();
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}

	private int getUniqueId() {
		int id = 1;
		while (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id)) {
			id++;
		}
		return id;
	}

	private void updateEpicStatus(int epicId) {
		Epic epic = epics.get(epicId);
		List<Integer> subs = epic.getSubtaskIds();
		if (subs.isEmpty()) {
			epic.setStatus(NEW);
			return;
		}
		TaskStatus status = null;
		for (int id : subs) {
			final Subtask subtask = subtasks.get(id);
			if (status == null) {
				status = subtask.getStatus();
				continue;
			}

			if (status == subtask.getStatus()
					&& status != IN_PROGRESS) {
				continue;
			}
			epic.setStatus(IN_PROGRESS);
			return;
		}
		epic.setStatus(status);
	}
}
