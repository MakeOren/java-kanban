package manager;

import exceptions.NotFoundException;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;

public interface TaskManager {
    List<SubTask> getSubTasksByEpic(int epicId);

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    SubTask addSubTask(SubTask subTask);

    List<Task> getTasksList();

    List<SubTask> getSubTasksList();

    List<Epic> getEpicList();

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubTaskById(int subTaskId);

    Task updateTask(Task task);

    void updateEpic(Epic epic);

    SubTask updateSubTask(SubTask subTask);

    void deleteTaskById(int taskId);

    void deleteEpicById(int epicId);

    void deleteSubTaskById(int subTaskId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    Task getTaskOrThrow(int id) throws NotFoundException;

    Epic getEpicOrThrow(int id) throws NotFoundException;

    SubTask getSubTaskOrThrow(int id) throws NotFoundException;


}
