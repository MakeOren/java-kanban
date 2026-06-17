package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            file = File.createTempFile("tasks", ".csv");
            file.deleteOnExit();
            return (FileBackedTaskManager) Managers.getFileBackedTaskManager(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loaded.getTasksList().isEmpty());
        assertTrue(loaded.getEpicList().isEmpty());
        assertTrue(loaded.getSubTasksList().isEmpty());
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task = new Task("Задача1", "Описание1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        task.setDuration(Duration.ofMinutes(30));

        Epic epic = new Epic("Эпик1", "Описание1");
        taskManager.addTask(task);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());
        subTask.setStartTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        subTask.setDuration(Duration.ofMinutes(45));
        taskManager.addSubTask(subTask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getTasksList().size());
        assertEquals(1, loaded.getEpicList().size());
        assertEquals(1, loaded.getSubTasksList().size());

        assertEquals("Задача1", loaded.getTasksList().get(0).getTitle());
        assertEquals("Эпик1", loaded.getEpicList().get(0).getTitle());
        assertEquals("Подзадача1", loaded.getSubTasksList().get(0).getTitle());

        Task loadedTask = loaded.getTasksList().get(0);
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), loadedTask.getStartTime());
        assertEquals(Duration.ofMinutes(30), loadedTask.getDuration());

        SubTask loadedSubTask = loaded.getSubTasksList().get(0);
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 0), loadedSubTask.getStartTime());
        assertEquals(Duration.ofMinutes(45), loadedSubTask.getDuration());

        assertEquals(epic.getId(), loadedSubTask.getEpicId());
    }

    @Test
    void testNextIdAfterLoad() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание", TaskStatus.NEW);
        taskManager.addTask(task2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Task task3 = new Task("Задача 3", "Описание", TaskStatus.NEW);
        loaded.addTask(task3);

        assertEquals(3, task3.getId());
    }

    @Test
    void testSaveException() {
        File invalidFile = new File("/invalid/path/file.csv");
        FileBackedTaskManager invalidManager = new FileBackedTaskManager(invalidFile);

        assertThrows(ManagerSaveException.class, () -> {
            invalidManager.addTask(new Task("Test", "Desc", TaskStatus.NEW));
        });
    }

    @Test
    void testSaveDoesNotThrowException() {
        assertDoesNotThrow(() -> {
            Task task = new Task("Test", "Desc", TaskStatus.NEW);
            taskManager.addTask(task);
        });
    }

}
