package taskmanager.controller.adapters;

import com.google.gson.*;
import taskmanager.model.Task;

import java.lang.reflect.Type;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();


        return null;
    }
}
