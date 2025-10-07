package ru.yandex.javacourse.schedule.manager;

/**
 * Default managers.
 *
 * @author Vladimir Ivanov (ivanov.vladimir.l@gmail.com)
 */
public class Managers {
	public static TaskManager getDefaultTaskManager() {
		return new InMemoryTaskManager();
	}

	public static HistoryManager getDefaultHistoryManager() {
		return new InMemoryHistoryManager();
	}
}
