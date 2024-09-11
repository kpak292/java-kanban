package taskmanager.controller.adapters;

import com.google.gson.*;
import taskmanager.model.Epic;
import taskmanager.model.Task;

import java.lang.reflect.Type;

public class EpicSerializer implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("ID", task.getId());
        result.addProperty("Type", task.getClass().getSimpleName());
        result.addProperty("Name", task.getDescription());
        result.addProperty("Description", task.getDescription());
        result.addProperty("Status", task.getStatus().name());
        result.addProperty("StartTime", task.getStartTime().format(Task.formatter));
        result.addProperty("Duration", task.getDuration().toMinutes());

        JsonArray subtaskIds = new JsonArray();
        result.add("SubtaskIDs", subtaskIds);

        for (Integer i : task.getSubtaskIds()) {
            subtaskIds.add(new JsonPrimitive(i));
        }

        return result;
    }
}
