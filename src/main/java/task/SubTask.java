package task;

public class SubTask extends Task{
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


    public SubTask(String title, String description, TaskStatus taskStatus) {
        super(title, description, taskStatus);
    }

    @Override
    public String toString() {
        return "Task.SubTask{" +
                "id=" + this.getId() + ", " +
                "taskStatus=" + this.getTaskStatus() + ", " +
                "title=" + this.getTitle() + ", " +
                "description=" + this.getDescription() + ", " +
                "epicId=" + this.epicId +
                '}';
    }
}
