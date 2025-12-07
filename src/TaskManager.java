import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int nextId;
    private Map<Integer,Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    private int getNextId() {
        return ++nextId;
    }

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

    public Task addTask(Task task) {
        int id = getNextId();
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    public Task addEpic(Epic epic) {
        int id = getNextId();
        epic.setId(id);
        epics.put(epic.getId(),epic);
        return epic;
    }

    public SubTask addSubTask(SubTask subTask, int epicId) {
        int id = getNextId();
        Epic epic = epics.get(epicId);
        List<Integer> subTaskIds = epic.getSubTaskIds();

        subTask.setId(id);
        subTask.setEpicId(epicId);
        subTasks.put(subTask.getId(), subTask);
        subTaskIds.add(subTask.getId());

        updateEpicStatus(epicId);

        return subTask;
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    public void clearEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId) {
        return  epics.get(epicId);
    }

    public  SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void updateTask(Task task, int taskId) {
        task.setId(taskId);

        tasks.put(taskId, task);
    }

    public void updateEpic(Epic epic, int epicId) {
        epic.setId(epicId);
        List<Integer> subTasksId = epics.get(epicId).getSubTaskIds();

        epic.setSubTaskIds(subTasksId);

        epics.put(epicId, epic);

        updateEpicStatus(epicId);
    }

    public void updateSubTask(SubTask subTask, int subTaskId) {
        int epicId = subTasks.get(subTaskId).getEpicId();

        subTask.setId(subTaskId);
        subTask.setEpicId(epicId);

        subTasks.put(subTaskId,subTask);

        updateEpicStatus(epicId);
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpicById(int epicId) {
        List<Integer> subTasksIds = epics.get(epicId).getSubTaskIds();

        for (Integer subTaskId : subTasksIds) {
            subTasks.remove(subTaskId);
        }

        epics.remove(epicId);
    }

    public void deleteSubTaskById(int subTaskId) {
        int epicId = subTasks.get(subTaskId).getEpicId();
        List<Integer> subTaskIds = epics.get(epicId).getSubTaskIds();

        subTaskIds.remove(subTaskId);
        subTasks.remove(subTaskId);

        updateEpicStatus(epicId);
    }

    
    


}
