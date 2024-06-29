package TaskManager;

public class Epic extends Task {
    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    //Метод для обновления статуса
    protected void updateStatus(TaskStatus status){
        this.taskStatus= status;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        System.out.println("Ошибка: Статус эпика зависит от статуса подзадач");
    }

    @Override
    public String toString() {
        return "TaskManager.Epic{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
