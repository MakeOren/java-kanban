package main;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача1", "Описание1", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание2", TaskStatus.NEW);

        Epic epic1 = new Epic("Эпик1","Описание1");
        Epic epic2 = new Epic("Эпик2","Описание2");



        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask1 = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        subTask1.setEpicId(epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача2", "Описание2", TaskStatus.NEW);
        subTask2.setEpicId(epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача3", "Описание3", TaskStatus.NEW);
        subTask3.setEpicId(epic2.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println();
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getEpicById(epic2.getId()));
        System.out.println();
        System.out.println(taskManager.getSubTaskById(subTask1.getId()));
        System.out.println(taskManager.getSubTaskById(subTask2.getId()));
        System.out.println(taskManager.getSubTaskById(subTask3.getId()));
        System.out.println();


        Task updateTask1 = new Task("Задача1", "Описание1", TaskStatus.DONE);
        updateTask1.setId(task1.getId());
        Task updateTask2 = new Task("Задача2", "Описание2", TaskStatus.IN_PROGRESS);
        updateTask2.setId(task2.getId());

        taskManager.updateTask(updateTask1);
        taskManager.updateTask(updateTask2);

        SubTask updateSubTask1 = new SubTask("Подзадача1", "Описание1", TaskStatus.DONE);
        updateSubTask1.setId(subTask1.getId());
        SubTask updateSubTask2 = new SubTask("Подзадача2", "Описание2", TaskStatus.IN_PROGRESS);
        updateSubTask2.setId(subTask2.getId());
        SubTask updateSubTask3 = new SubTask("Подзадача3", "Описание3", TaskStatus.DONE);
        updateSubTask3.setId(subTask3.getId());

        taskManager.updateSubTask(updateSubTask1);
        taskManager.updateSubTask(updateSubTask2);
        taskManager.updateSubTask(updateSubTask3);

        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println();
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getEpicById(epic2.getId()));
        System.out.println();
        System.out.println(taskManager.getSubTaskById(subTask1.getId()));
        System.out.println(taskManager.getSubTaskById(subTask2.getId()));
        System.out.println(taskManager.getSubTaskById(subTask3.getId()));
        System.out.println();

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println(taskManager.getSubTaskById(task1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(taskManager.getSubTaskById(subTask1.getId()));
    }
}
