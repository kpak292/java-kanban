package taskmanager.model;

import java.time.Duration;
import java.util.Objects;

public class Subtask extends Task {
    //ID эпика, подзадача не может существовать без Эпика
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
    public Task clone() {
        Subtask clone = new Subtask(this.name, this.description, this.epicId);
        clone.id = this.id;
        clone.status = this.status;
        clone.setStartTime(this.startTime);
        clone.setDuration(Duration.ofMinutes(this.duration));

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(transform(id))
                .append(";")
                .append(transform("Subtask"))
                .append(";")
                .append(transform(name))
                .append(";")
                .append(transform(description))
                .append(";")
                .append(transform(status.toString()))
                .append(";")
                .append(startTime == null ? "" : transform(startTime.format(formatter)))
                .append(";")
                .append(transform(duration))
                .append(";")
                .append(getEndTime() == null ? "" : transform(getEndTime().format(formatter)))
                .append(";")
                .append(";")
                .append(transform(epicId));
        return builder.toString();
    }

}
