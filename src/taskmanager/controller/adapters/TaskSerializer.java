package taskmanager.controller.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import taskmanager.model.Task;

import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
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

        return result;
    }
}
