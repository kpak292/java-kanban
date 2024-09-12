package taskmanager.controller.adapters;

import com.google.gson.*;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();

        //Check for fields
        if (!object.has("Name") ||
                !object.has("Description") ||
                !object.has("Type") ||
                (object.get("Type").getAsString().equals(taskmanager.model.enums.Type.SUBTASK.name()) &&
                        !object.has("EpicID"))) {
            throw new IllegalArgumentException("Error: Illegal Arguments");
        }

        String name = object.get("Name").getAsString();
        String description = object.get("Description").getAsString();

        int id;
        if (object.has("ID")) {
            id = object.get("ID").getAsInt();
        } else {
            id = 0;
        }
        Status status;
        if (object.has("Status")) {
            status = Status.valueOf(object.get("Status").getAsString());
        } else {
            status = Status.NEW;
        }

        LocalDateTime startTime;
        try {
            startTime = LocalDateTime.parse(object.get("StartTime").getAsString(), Task.formatter);
        } catch (Exception e) {
            startTime = null;
        }

        Duration duration;
        try {
             duration = Duration.ofMinutes(object.get("Duration").getAsInt());
        }catch (Exception e){
            duration = Duration.ofMinutes(0);
        }

        Task task;

        String taskType = object.get("Type").getAsString();
        taskmanager.model.enums.Type instanceType = taskmanager.model.enums.Type.valueOf(taskType);

        switch (instanceType) {
            case SUBTASK -> {
                int epicID = object.get("EpicID").getAsInt();
                task = new Subtask(name, description, epicID);
            }
            case EPIC -> {
                task = new Epic(name, description);

                if (object.has("SubtaskIDs")) {
                    List<Integer> epicSubs = ((Epic) task).getSubtaskIds();
                    JsonArray array = object.get("SubtaskIDs").getAsJsonArray();

                    for (JsonElement subsID : array) {
                        epicSubs.add(subsID.getAsInt());
                    }
                }
            }
            default -> task = new Task(name, description);
        }

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        return task;
    }
}
