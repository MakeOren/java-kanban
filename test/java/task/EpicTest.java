package task;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private TaskManager manager;


    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Задача1", "Описание1");
        Epic epic2 = new Epic("Задача2", "Описание2");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);

    }

    @Test
    void epicStatusShouldBeNewWhenAllSubtasksNew() {

        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Подзадача1", "Описание", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Подзадача2", "Описание", TaskStatus.NEW);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(TaskStatus.NEW, manager.getEpicById(epic.getId()).getTaskStatus());
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Подзадача1", "Описание", TaskStatus.DONE);
        SubTask subTask2 = new SubTask("Подзадача2", "Описание", TaskStatus.DONE);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(TaskStatus.DONE, manager.getEpicById(epic.getId()).getTaskStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenSubtasksNewAndDone() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Подзадача1", "Описание", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Подзадача2", "Описание", TaskStatus.DONE);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getTaskStatus());
    }

    @Test
    void epicStatusShouldBeInProgressWhenAtLeastOneSubtaskInProgress() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("Подзадача1", "Описание", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Подзадача2", "Описание", TaskStatus.IN_PROGRESS);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getTaskStatus());
    }

    @Test
    void epicStatusShouldBeNewWhenNoSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        assertEquals(TaskStatus.NEW, manager.getEpicById(epic.getId()).getTaskStatus());
    }
}