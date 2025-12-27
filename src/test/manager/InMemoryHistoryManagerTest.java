package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldNotChangeTaskFieldsWhenAddedToHistoryManager() {
        Task task = new Task("Задача1", "Описание1", TaskStatus.NEW);

        Epic epic = new Epic("Эпик1","Описание1");

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        subTask.setEpicId(epic.getId());

        taskManager.addSubTask(subTask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());

        List<Task> historyTask = historyManager.getHistory();

        for (Task task1 : historyTask) {
            if (task1.equals(task)) {
                assertEquals(task.getId(), task1.getId());
                assertEquals(task.getTitle(), task1.getTitle());
                assertEquals(task.getDescription(), task1.getDescription());
                assertEquals(task.getTaskStatus(), task1.getTaskStatus());
            } else if (task1.equals(epic)) {
                Epic epic1 = (Epic) task1;

                assertEquals(epic.getId(), epic1.getId());
                assertEquals(epic.getSubTaskIds(), epic1.getSubTaskIds());
                assertEquals(epic.getTaskStatus(), epic1.getTaskStatus());
                assertEquals(epic.getTitle(), epic1.getTitle());
                assertEquals(epic.getDescription(), epic1.getDescription());
            } else if (task1.equals(subTask)) {
                SubTask subTask1 = (SubTask) task1;

                assertEquals(subTask.getId(), subTask1.getId());
                assertEquals(subTask.getTitle(), subTask1.getTitle());
                assertEquals(subTask.getDescription(), subTask1.getDescription());
                assertEquals(subTask.getTaskStatus(), subTask1.getTaskStatus());
                assertEquals(subTask.getEpicId(), subTask1.getEpicId());

            }
        }

    }

}