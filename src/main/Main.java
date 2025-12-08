package main;

import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача1", "Описание1", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание2", TaskStatus.NEW);

        Epic epic1 = new Epic("Эпик1","Описание1");
        SubTask subTask1 = new SubTask("Подзадача1", "Описание1", TaskStatus.NEW);
        SubTask subTask2 = new SubTask("Подзадача2", "Описание2", TaskStatus.NEW);

        Epic epic2 = new Epic("Эпик2","Описание1");
        SubTask subTask3 = new SubTask("Подзадача3", "Описание3", TaskStatus.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.addSubTask(subTask1,epic1.getId());
        taskManager.addSubTask(subTask2,epic1.getId());
        taskManager.addSubTask(subTask3,epic2.getId());

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

        taskManager.updateTask(new Task("Задача1", "Описание1", TaskStatus.DONE), task1.getId());
        taskManager.updateTask(new Task("Задача2", "Описание2", TaskStatus.IN_PROGRESS), task2.getId());

        taskManager.updateSubTask(new SubTask("Подзадача1", "Описание1", TaskStatus.DONE), subTask1.getId());
        taskManager.updateSubTask(new SubTask("Подзадача2", "Описание2", TaskStatus.IN_PROGRESS), subTask2.getId());
        taskManager.updateSubTask(new SubTask("Подзадача3", "Описание3", TaskStatus.DONE),subTask3.getId());

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
