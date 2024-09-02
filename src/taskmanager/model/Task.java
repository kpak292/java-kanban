package taskmanager.model;

import taskmanager.model.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

//Основной класс задач,
public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status = Status.NEW;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getEndTime(){
        return startTime.plus(duration);
    }

    public Task clone() {
        Task clone = new Task(this.name, this.description);
        clone.setId(this.id);
        clone.setStatus(this.status);

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(transform(id))
                .append(";")
                .append(transform("Task"))
                .append(";")
                .append(transform(name))
                .append(";")
                .append(transform(description))
                .append(";")
                .append(transform(status.toString()))
                .append(";")
                .append(";");
        return builder.toString();
    }

    protected String transform(String text) {
        return "\"" + text + "\"";
    }

    protected String transform(int text) {
        return "\"" + text + "\"";
    }

}
