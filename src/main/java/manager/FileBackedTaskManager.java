package manager;

import task.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public Task addTask(Task task) {
        Task result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public Task addEpic(Epic epic) {
        Task result = super.addEpic(epic);
        save();
        return result;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask result = super.addSubTask(subTask);
        save();
        return result;
    }


    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

    private String toString(Task task) {

        if (task instanceof SubTask) {
            return String.format("%s,%s,%s,%s,%s,%d",
                    task.getId(),
                    TaskType.SUBTASK,
                    task.getTitle(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    ((SubTask) task).getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%s,%s,%s,%s,%s,",
                    task.getId(),
                    TaskType.EPIC,
                    task.getTitle(),
                    task.getTaskStatus(),
                    task.getDescription());
        } else {
            return String.format("%s,%s,%s,%s,%s,",
                    task.getId(),
                    TaskType.TASK,
                    task.getTitle(),
                    task.getTaskStatus(),
                    task.getDescription());
        }
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        switch (taskType) {
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setTaskStatus(taskStatus);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                SubTask subTask = new SubTask(title, description, taskStatus);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                return subTask;
            }
            case TASK -> {
                Task task = new Task(title, description, taskStatus);
                task.setId(id);
                return task;
            }
            default -> {
                return null;
            }
        }

    }

    private void save() {
        List<Epic> epics = getEpicList();
        List<SubTask> subTasks = getSubTasksList();
        List<Task> tasks = getTasksList();

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            bf.write("id,type,name,status,description,epic");
            bf.newLine();

            for (Task task : tasks) {
                bf.write(toString(task));
                bf.newLine();
            }

            for (Epic epic : epics) {
                bf.write(toString(epic));
                bf.newLine();
            }

            for (SubTask subTask : subTasks) {
                bf.write(toString(subTask));
                bf.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getPath(), e);
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            bf.readLine();
            String line;
            Map<Integer, Task> tasks = new HashMap<>();
            Map<Integer, Epic> epics = new HashMap<>();
            Map<Integer, SubTask> subTasks = new HashMap<>();

            while ((line = bf.readLine()) != null) {
                Task task = fileBackedTaskManager.fromString(line);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task instanceof Epic epic) {
                    epics.put(epic.getId(), epic);
                } else if (task instanceof SubTask subTask) {
                    subTasks.put(subTask.getId(), subTask);
                } else {
                    tasks.put(task.getId(), task);
                }
            }

            for (Map.Entry<Integer, SubTask> entrySubTask : subTasks.entrySet()) {
                Epic epic = epics.get(entrySubTask.getValue().getEpicId());
                epic.addSubTaskId(entrySubTask.getKey());
            }

            fileBackedTaskManager.setTasks(tasks);
            fileBackedTaskManager.setEpics(epics);
            fileBackedTaskManager.setSubTasks(subTasks);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла: " + file.getPath(), e);
        }

        fileBackedTaskManager.setNextId(maxId);

        return fileBackedTaskManager;
    }

}
