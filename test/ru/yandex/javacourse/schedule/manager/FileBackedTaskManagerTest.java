package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.TaskStubs;
import ru.yandex.javacourse.schedule.manager.exception.ManagerLoadException;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private final static String TEMP_FILE_SUFFIX = ".txt";
    private final static String TEST_FILE_CONTENT = """
            S,4,Test 1,N,Description 1,5\
            
            S,3,Test 3,N,Description 3,5\
            
            E,5,Test 1,N,Description 1,\
            
            T,2,Test 2,N,Description 2,\
            
            T,1,Test 1,N,Description 1,""";

    @Test
    @DisplayName("Проверка сохранения пустого файла(без задач)")
    public void save_SaveEmptyFile_FileIsEmpty() throws IOException {
        // given
        File tmpFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), TEMP_FILE_SUFFIX);
        TaskManager manager = Managers.getFileBackedTaskManager(tmpFile);
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        // when
        manager.addNewTask(task);
        manager.deleteTask(task.getId());
        FileReader fr = new FileReader(tmpFile);
        int firstCharacterInFile = fr.read();
        fr.close();
        // then
        assertEquals(-1, firstCharacterInFile, "file shouldn't contain any tasks after delete!");
    }

    @Test
    @DisplayName("Проверка загрузки пустого фала")
    public void load_LoadEmptyFile_SuccessLoad() throws IOException {
        // given
        File tmpFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), TEMP_FILE_SUFFIX);
        // when
        Exception loadException = null;
        try {
            Managers.getFileBackedTaskManager(tmpFile);
        } catch (ManagerLoadException e) {
            loadException = e;
        }
        // then
        assertNull(loadException, "shouldn't throw ManagerLoadException on empty file!");
    }

    @Test
    @DisplayName("Проверка cохранения задач в файл")
    public void save_SaveSeveralTasksToFile_SuccessFileSave() throws IOException {
        // given
        File tmpFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), TEMP_FILE_SUFFIX);
        TaskManager manager = Managers.getFileBackedTaskManager(tmpFile);
        Task task = new Task(TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW);
        Subtask task3 = new Subtask(3, TaskStubs.TASK_NAME_3, TaskStubs.TASK_DESCRIPTION_3, TaskStatus.NEW, 5);
        Subtask task4 = new Subtask(4, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1, TaskStatus.NEW, 5);
        Epic epic = new Epic(5, TaskStubs.TASK_NAME_1, TaskStubs.TASK_DESCRIPTION_1);
        // when
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewTask(task);
        manager.addNewTask(TaskStubs.TASK_STUB_2);
        manager.addNewEpic(epic);
        manager.addNewSubtask(task3);
        manager.addNewSubtask(task4);
        BufferedReader br = new BufferedReader(new FileReader(tmpFile));
        String fileContent = br.lines().reduce((line, acc) -> acc + '\n' + line).get();
        br.close();
        // then
        assertEquals(TEST_FILE_CONTENT, fileContent, "file has wrong tasks");
    }

    @Test
    @DisplayName("Проверка чтения задач из файла")
    public void load_ReadSeveralTasksFromFile_AllTasksRead() throws IOException {
        // given
        File tmpFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), TEMP_FILE_SUFFIX);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write(TEST_FILE_CONTENT);
        bw.close();
        // when
        TaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);
        // then
        assertEquals(2, manager.getTasks().size(), "wrong tasks count!");
        assertEquals(2, manager.getSubtasks().size(), "wrong subtasks count!");
        assertEquals(1, manager.getEpics().size(), "wrong epics count!");
    }
}
