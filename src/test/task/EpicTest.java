package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Задача1","Описание1");
        Epic epic2 = new Epic("Задача2","Описание2");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1,epic2);

    }
}