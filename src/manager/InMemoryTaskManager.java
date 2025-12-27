package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId;
    private Map<Integer,Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, SubTask> subTasks;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.nextId = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }


    private int getNextId() {
        int id = ++nextId;

        while (tasks.containsKey(id) || epics.containsKey(id) || subTasks.containsKey(id)) {
            id++;
        }
        nextId = id;

        return id;
    }



    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTasksList = new ArrayList<>();

        for (int subTaskId : epic.getSubTaskIds()) {
            subTasksList.add(subTasks.get(subTaskId));
        }

        return subTasksList;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTaskList = getSubTasksByEpic(epicId);
        
        if (subTaskList.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : subTaskList) {
            if (!subTask.getTaskStatus().equals(TaskStatus.NEW)) {
                allNew = false;
            }

            if (!subTask.getTaskStatus().equals(TaskStatus.DONE)) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        }else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public Task addTask(Task task) {
        int id = getNextId();
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task addEpic(Epic epic) {
        int id = getNextId();
        epic.setId(id);
        epics.put(epic.getId(),epic);
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        int id = getNextId();
        int epicId = subTask.getEpicId();

        if (id == epicId) {
            return null;
        }

        Epic epic = epics.get(epicId);
        List<Integer> subTaskIds = epic.getSubTaskIds();

        subTask.setId(id);
        subTask.setEpicId(epicId);
        subTasks.put(subTask.getId(), subTask);
        subTaskIds.add(subTask.getId());

        updateEpicStatus(epicId);

        return subTask;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void clearEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);

        historyManager.add(task);

        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);

        historyManager.add(epic);

        return  epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);

        historyManager.add(subTask);

        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();

        tasks.put(taskId, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        List<Integer> subTasksId = epics.get(epicId).getSubTaskIds();

        epic.setSubTaskIds(subTasksId);

        epics.put(epicId, epic);

        updateEpicStatus(epicId);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        int epicId = subTasks.get(subTaskId).getEpicId();

        subTask.setEpicId(epicId);

        subTasks.put(subTaskId,subTask);

        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        List<Integer> subTasksIds = epics.get(epicId).getSubTaskIds();

        for (Integer subTaskId : subTasksIds) {
            subTasks.remove(subTaskId);
        }

        epics.remove(epicId);
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        int epicId = subTasks.get(subTaskId).getEpicId();
        List<Integer> subTaskIds = epics.get(epicId).getSubTaskIds();

        subTaskIds.remove(subTaskId);
        subTasks.remove(subTaskId);

        updateEpicStatus(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
