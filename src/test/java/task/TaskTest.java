package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Задача1","Описание1", TaskStatus.NEW);
        Task task2 = new Task("Задача2","Описание2", TaskStatus.IN_PROGRESS);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1,task2);
    }
}
