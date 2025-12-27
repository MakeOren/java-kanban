package manager;

import task.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new LinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(taskHistory);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (taskHistory.size() >= 10) {
            taskHistory.remove(0);
        }

        taskHistory.add(task);
    }
}
