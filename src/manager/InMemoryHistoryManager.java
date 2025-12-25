package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    List<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }

    @Override
    public void add(Task task) {
        if (taskHistory.size() >= 10) {
            taskHistory.remove(0);
        }

        taskHistory.add(task);
    }
}
