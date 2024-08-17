package taskmanager.model;

import java.util.ArrayList;
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
        return "Model.Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
