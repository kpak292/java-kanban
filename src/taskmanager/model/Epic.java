package taskmanager.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public Task clone() {
        Epic clone = new Epic(this.name, this.description);
        clone.id = this.id;
        clone.status = this.status;

        clone.subtaskIds.addAll(this.subtaskIds);

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(transform(id))
                .append(";")
                .append(transform("Epic"))
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
                .append(transform(Arrays.toString(subtaskIds.toArray())))
                .append(";");
        return builder.toString();
    }
}
