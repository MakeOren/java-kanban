package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        manager = (FileBackedTaskManager) Managers.getFileBackedTaskManager(file);
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
        Epic epic = new Epic("Эпик1", "Описание1");

        manager.addTask(task);
        manager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);

        subTask.setEpicId(epic.getId());

        manager.addSubTask(subTask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getTasksList().size());
        assertEquals(1, loaded.getEpicList().size());
        assertEquals(1, loaded.getSubTasksList().size());

        // Проверяем, что данные сохранились
        assertEquals("Задача1", loaded.getTasksList().get(0).getTitle());
        assertEquals("Эпик1", loaded.getEpicList().get(0).getTitle());
        assertEquals("Подзадача1", loaded.getSubTasksList().get(0).getTitle());

        SubTask loadedSubTask = loaded.getSubTasksList().get(0);
        assertEquals(epic.getId(), loadedSubTask.getEpicId());
    }

    @Test
    void testNextIdAfterLoad() {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание", TaskStatus.NEW);
        manager.addTask(task2);

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
    void testUpdateTask() {
        Task task = new Task("Старое имя", "Описание", TaskStatus.NEW);
        manager.addTask(task);

        task.setTitle("Новое имя");
            

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertEquals("Новое имя", loaded.getTaskById(task.getId()).getTitle());
    }
}
