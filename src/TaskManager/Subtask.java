package TaskManager;

public class Subtask extends Task {
    //Ссылка на эпик, подзадача не может существовать без Эпика
    private final int epicId;

    public Subtask(String taskName, String taskDescription, int epicId) {
        super(taskName, taskDescription);
        this.epicId = epicId;
    }

    //Получение ссылки на эпик
    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "TaskManager.Subtask{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
