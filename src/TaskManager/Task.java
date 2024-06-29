package TaskManager;

import java.util.Objects;

//Основной класс задач,
public class Task {

    //Параметры
    protected int taskID;
    protected String taskName;
    protected String taskDescription;
    protected TaskStatus taskStatus;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = TaskStatus.NEW;
    }

    public int getTaskID() {
        return taskID;
    }

    //Закрываем доступность, ИД будет присваиваться только менеджером
    protected void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    //Чтобы избежать, что все новосозданные задачи будут с ИД 0, предусматриваем чтоб они не равны
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (task.getTaskID() == 0) return false;
        return taskID == task.taskID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskID);
    }

    @Override
    public String toString() {
        return "TaskManager.Task{" +
                "taskID=" + taskID +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }

}
