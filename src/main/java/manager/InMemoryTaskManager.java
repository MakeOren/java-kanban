package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int nextId;
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, SubTask> subTasks;
    private HistoryManager historyManager;
    private Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.nextId = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private void updateEpicTimings(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTaskList = getSubTasksByEpic(epicId);

        if (subTaskList.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earliestStart = subTaskList.stream()
                .map(subTask -> subTask.getStartTime())
                .filter(startTime -> startTime != null)
                .min((time1, time2) -> time1.compareTo(time2))
                .orElse(null);

        LocalDateTime latestEnd = subTaskList.stream()
                .map(subTask -> subTask.getEndTime())
                .filter(endTime -> endTime != null)
                .max((time1, time2) -> time1.compareTo(time2))
                .orElse(null);

        Duration totalDuration = subTaskList.stream()
                .map(subTask -> (subTask.getDuration()))
                .filter(duration -> (duration != null))
                .reduce(Duration.ZERO, (Duration::plus));

        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
        epic.setDuration(totalDuration);
    }

    protected void setTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void setEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void setSubtask(SubTask subtask) {
        subTasks.put(subtask.getId(), subtask);
    }

    protected void setNextId(int nextId) {
        this.nextId = nextId;
    }

    protected void setSubTasks(Map<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    protected void setEpics(Map<Integer, Epic> epics) {
        this.epics = epics;
    }

    protected void setTasks(Map<Integer, Task> tasks) {
        this.tasks = tasks;
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
        return epics.get(epicId).getSubTaskIds().stream()
                .map(subTaskId -> subTasks.get(subTaskId))
                .toList();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<SubTask> subTaskList = getSubTasksByEpic(epicId);

        if (subTaskList.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subTaskList.stream()
                .allMatch(subTask -> subTask.getTaskStatus() == TaskStatus.NEW);
        boolean allDone = subTaskList.stream()
                .allMatch(subTask -> subTask.getTaskStatus() == TaskStatus.DONE);

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public Task addTask(Task task) {
        if (isIntersectWithAny(task)) {
            return null;
        }

        int id = getNextId();
        task.setId(id);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task addEpic(Epic epic) {
        if (isIntersectWithAny(epic)) {
            return null;
        }

        int id = getNextId();
        epic.setId(id);

        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }

        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (isIntersectWithAny(subTask)) {
            return null;
        }

        int id = getNextId();
        int epicId = subTask.getEpicId();

        if (id == epicId) {
            return null;
        }

        Epic epic = epics.get(epicId);
        List<Integer> subTaskIds = epic.getSubTaskIds();

        subTask.setId(id);
        subTask.setEpicId(epicId);

        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }

        subTasks.put(subTask.getId(), subTask);
        subTaskIds.add(subTask.getId());

        updateEpicStatus(epicId);
        updateEpicTimings(epicId);

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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }

        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }

        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            epic.setTaskStatus(TaskStatus.NEW);
            updateEpicTimings(epic.getId());
        }
    }

    @Override
    public void clearEpics() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }

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

        return epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);

        historyManager.add(subTask);

        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        if (isIntersectWithAny(task)) {
            return;
        }

        int taskId = task.getId();

        Task oldTask = tasks.get(taskId);
        if (oldTask != null && oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask);
        }

        tasks.put(taskId, task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (isIntersectWithAny(epic)) {
            return;
        }

        int epicId = epic.getId();

        Epic oldEpic = epics.get(epicId);

        if (oldEpic != null && oldEpic.getStartTime() != null) {
            prioritizedTasks.remove(oldEpic);
        }

        List<Integer> subTasksId = epics.get(epicId).getSubTaskIds();

        epic.setSubTaskIds(subTasksId);

        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }

        epics.put(epicId, epic);

        updateEpicStatus(epicId);
        updateEpicTimings(epicId);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (isIntersectWithAny(subTask)) {
            return;
        }

        int subTaskId = subTask.getId();
        int epicId = subTasks.get(subTaskId).getEpicId();

        SubTask oldSubTask = subTasks.get(subTaskId);
        if (oldSubTask != null && oldSubTask.getStartTime() != null) {
            prioritizedTasks.remove(oldSubTask);
        }

        subTask.setEpicId(epicId);

        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }

        subTasks.put(subTaskId, subTask);

        updateEpicStatus(epicId);
        updateEpicTimings(epicId);
    }

    @Override
    public void deleteTaskById(int taskId) {
        Task task = tasks.get(taskId);

        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        List<Integer> subTasksIds = epics.get(epicId).getSubTaskIds();
        Epic epic = epics.get(epicId);

        for (Integer subTaskId : subTasksIds) {
            SubTask subTask = subTasks.get(subTaskId);

            if (subTask != null && subTask.getStartTime() != null) {
                prioritizedTasks.remove(subTask);
            }
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }

        if (epic != null && epic.getStartTime() != null) {
            prioritizedTasks.remove(epic);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        int epicId = subTasks.get(subTaskId).getEpicId();
        List<Integer> subTaskIds = epics.get(epicId).getSubTaskIds();

        SubTask subTask = subTasks.get(subTaskId);

        if (subTask != null && subTask.getStartTime() != null) {
            prioritizedTasks.remove(subTask);
        }
        subTaskIds.remove(subTaskId);
        subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);

        updateEpicStatus(epicId);
        updateEpicTimings(epicId);

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null || task1.getId() == task2.getId()) {
            return false;
        }

        LocalDateTime startTimeTask1 = task1.getStartTime();
        LocalDateTime endTimeTask1 = task1.getEndTime();
        LocalDateTime startTimeTask2 = task2.getStartTime();
        LocalDateTime endTimeTask2 = task2.getEndTime();

        if (endTimeTask1.isBefore(startTimeTask2) || endTimeTask2.isBefore(startTimeTask1)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isIntersectWithAny(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }

        return prioritizedTasks.stream().filter(task -> task.getId() != newTask.getId())
                .anyMatch(task -> isIntersect(task, newTask));
    }
}
