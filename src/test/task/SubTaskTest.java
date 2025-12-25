package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubTaskTest {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        SubTask subTask1 = new SubTask("Задача1","Описание1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Задача2","Описание2", TaskStatus.IN_PROGRESS);

        subTask1.setId(1);
        subTask2.setId(1);

        assertEquals(subTask1,subTask2);
    }
}
