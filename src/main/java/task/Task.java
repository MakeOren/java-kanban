package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private TaskStatus taskStatus;
    private String title;
    private String description;

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        } else {
            return startTime.plus(this.duration);
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    private Duration duration;
    private LocalDateTime startTime;

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task(String title, String description, TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
        this.title = title;
        this.description = description;
        this.duration = Duration.ZERO;     // добавить
        this.startTime = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return this.getId() == task.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task.Task{" +
                "id=" + id +
                ", taskStatus=" + taskStatus +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}
