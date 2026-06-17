package manager;

import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";

        if (task instanceof SubTask) {
            return String.format("%s,%s,%s,%s,%s,%d,%s,%s",
                    task.getId(),
                    TaskType.SUBTASK,
                    task.getTitle(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    ((SubTask) task).getEpicId(),
                    duration,
                    startTime);
        } else if (task instanceof Epic) {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    TaskType.EPIC,
                    task.getTitle(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    "",
                    duration,
                    startTime);
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    TaskType.TASK,
                    task.getTitle(),
                    task.getTaskStatus(),
                    task.getDescription(),
                    "",
                    duration,
                    startTime);
        }
    }

    private Task fromString(String value) {
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        String durationStr = parts[6].isEmpty() ? "0" : parts[6];
        String startTimeStr = parts[7].isEmpty() ? null : parts[7];

        Duration duration = Duration.ofMinutes(Long.parseLong(durationStr));
        LocalDateTime startTime = startTimeStr != null ? LocalDateTime.parse(startTimeStr) : null;

        switch (taskType) {
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setTaskStatus(taskStatus);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                SubTask subTask = new SubTask(title, description, taskStatus);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                subTask.setDuration(duration);
                subTask.setStartTime(startTime);
                return subTask;
            }
            case TASK -> {
                Task task = new Task(title, description, taskStatus);
                task.setId(id);
                task.setDuration(duration);
                task.setStartTime(startTime);
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
            bf.write("id,type,name,status,description,epic,duration,startTime");
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

            while ((line = bf.readLine()) != null) {
                Task task = fileBackedTaskManager.fromString(line);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                switch (task.getType()) {
                    case TASK -> fileBackedTaskManager.setTask(task);
                    case EPIC -> fileBackedTaskManager.setEpic((Epic) task);
                    case SUBTASK -> fileBackedTaskManager.setSubtask((SubTask) task);
                }

            }

            for (SubTask subTask : fileBackedTaskManager.getSubTasksList()) {
                Epic epic = fileBackedTaskManager.getEpicById(subTask.getEpicId());
                epic.addSubTaskId(subTask.getId());
            }


        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла: " + file.getPath(), e);
        }

        fileBackedTaskManager.setNextId(maxId);

        return fileBackedTaskManager;
    }

}
