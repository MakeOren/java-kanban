package task;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subTaskIds;

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        subTaskIds = new ArrayList<>();
    }

    @Override
    public String toString() {

        return "Task.Task.Epic{" +
                "id=" + this.getId() + ", " +
                "taskStatus=" + this.getTaskStatus() + ", " +
                "title=" + this.getTitle() + ", " +
                "description=" + this.getDescription() +
                '}';
    }
}
