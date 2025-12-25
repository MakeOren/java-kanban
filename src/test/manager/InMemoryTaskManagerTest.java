package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void epicNotSubtaskOfItself() {
        Epic epic = new Epic("Эпик","Описание");
        SubTask subTask = new SubTask("Подзадача","Описание", TaskStatus.NEW);

        taskManager.addEpic(epic);

        subTask.setEpicId(epic.getId());
        subTask.setId(epic.getId());

        assertNotNull(taskManager.addSubTask(subTask), "Подзадача должна добавится");

        assertEquals(epic.getId(),subTask.getEpicId(), "Подзадача привязана к эпику");
        assertNotEquals(epic.getId(), subTask.getId(), "Id подзадачи отличается от id эпика");
    }

    @Test
    void managersShouldReturnInitializedInstances() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager);
        assertNotNull(historyManager);
    }

    @Test
    void addAndRetrieveTasksById() {
        Task task = new Task("Задача1", "Описание1", TaskStatus.NEW);

        Epic epic = new Epic("Эпик1","Описание1");

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());

        taskManager.addSubTask(subTask);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(subTask, taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    void taskIdsAreUniqueOrOverwritten() {
        Task task1 = new Task("Задача1", "Описание1", TaskStatus.NEW);

        taskManager.addTask(task1);

        Task task2 = new Task("Задача2", "Описание2", TaskStatus.NEW);
        task2.setId(task1.getId());

        taskManager.addTask(task2);


    }

    @Test
    void shouldHaveUniqueIdsForAllTasks() {
        Task task = new Task("Задача1", "Описание1", TaskStatus.NEW);

        Epic epic = new Epic("Эпик1","Описание1");

        taskManager.addTask(task);
        task.setId(2);
        taskManager.addTask(task);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());

        taskManager.addSubTask(subTask);

        assertNotEquals(task.getId(), epic.getId());
        assertNotEquals(task.getId(), subTask.getId());
        assertNotEquals(epic.getId(), subTask.getId());
    }

    @Test
    void shouldNotChangeTaskFieldsWhenAddedToTaskManager() {
        Task task = new Task("Задача1", "Описание1", TaskStatus.NEW);

        Epic epic = new Epic("Эпик1","Описание1");

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());

        taskManager.addSubTask(subTask);

        assertEquals(task.getTitle(), taskManager.getTaskById(task.getId()).getTitle());
        assertEquals(task.getDescription(), taskManager.getTaskById(task.getId()).getDescription());
        assertEquals(task.getTaskStatus(), taskManager.getTaskById(task.getId()).getTaskStatus());

        assertEquals(epic.getSubTaskIds(), taskManager.getEpicById(epic.getId()).getSubTaskIds());
        assertEquals(epic.getTaskStatus(), taskManager.getEpicById(epic.getId()).getTaskStatus());
        assertEquals(epic.getTitle(), taskManager.getEpicById(epic.getId()).getTitle());
        assertEquals(epic.getDescription(), taskManager.getEpicById(epic.getId()).getDescription());

        assertEquals(subTask.getTitle(), taskManager.getSubTaskById(subTask.getId()).getTitle());
        assertEquals(subTask.getDescription(), taskManager.getSubTaskById(subTask.getId()).getDescription());
        assertEquals(subTask.getTaskStatus(), taskManager.getSubTaskById(subTask.getId()).getTaskStatus());
        assertEquals(subTask.getEpicId(), taskManager.getSubTaskById(subTask.getId()).getEpicId());


    }

}