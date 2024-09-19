package taskmanager.controller.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import taskmanager.model.Subtask;
import taskmanager.model.Task;

import java.lang.reflect.Type;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("ID", task.getId());
        result.addProperty("Type", task.getClass().getSimpleName().toUpperCase());
        result.addProperty("Name", task.getName());
        result.addProperty("Description", task.getDescription());
        result.addProperty("Status", task.getStatus().name());

        if (task.getStartTime() != null) {
            result.addProperty("StartTime", task.getStartTime().format(Task.formatter));
        }

        result.addProperty("Duration", task.getDuration().toMinutes());

        result.addProperty("EpicID", task.getEpicId());

        return result;
    }
}
