import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import taskmanager.controller.adapters.EpicSerializer;
import taskmanager.controller.adapters.SubtaskSerializer;
import taskmanager.controller.adapters.TaskDeserializer;
import taskmanager.controller.adapters.TaskSerializer;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdaptersTest {
    GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer());

    Gson gson = builder.create();

    @Test
    public void shouldSearilizeTask() {
        Task task = new Task("name", "description");

        String result = gson.toJson(task);
        String expected = """
                {
                  "ID": 0,
                  "Type": "TASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        assertEquals(result, expected);
    }

    @Test
    public void shouldSearilizeTaskWithAdditionalData() {
        Task task = new Task("name", "description");
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 00));
        task.setDuration(Duration.ofMinutes(90));
        task.setId(100);
        task.setStatus(Status.DONE);

        String result = gson.toJson(task);
        String expected = """
                {
                  "ID": 100,
                  "Type": "TASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "DONE",
                  "StartTime": "01.01.2024 09:00",
                  "Duration": 90
                }""";

        assertEquals(result, expected);
    }

    @Test
    public void shouldSearilizeSubtask() {
        Task task = new Subtask("name", "description", 1);

        String result = gson.toJson(task);
        String expected = """
                {
                  "ID": 0,
                  "Type": "SUBTASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0,
                  "EpicID": 1
                }""";

        assertEquals(result, expected);
    }

    @Test
    public void shouldSearilizeSubtaskWithAdditionalData() {
        Task task = new Subtask("name", "description", 1);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 00));
        task.setDuration(Duration.ofMinutes(90));
        task.setId(100);
        task.setStatus(Status.DONE);

        String result = gson.toJson(task);
        String expected = """
                {
                  "ID": 100,
                  "Type": "SUBTASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "DONE",
                  "StartTime": "01.01.2024 09:00",
                  "Duration": 90,
                  "EpicID": 1
                }""";

        assertEquals(result, expected);
    }

    @Test
    public void shouldSearilizeEpic() {
        Epic task = new Epic("name", "description");

        List<Integer> subsID = task.getSubtaskIds();
        subsID.add(2);
        subsID.add(3);
        subsID.add(4);

        String result = gson.toJson(task);
        String expected = """
                {
                  "ID": 0,
                  "Type": "EPIC",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0,
                  "SubtaskIDs": [
                    2,
                    3,
                    4
                  ]
                }""";

        assertEquals(result, expected);
    }

    @Test
    public void shouldSearilizeEpicWithAdditionalData() {
        Epic task = new Epic("name", "description");
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 00));
        task.setDuration(Duration.ofMinutes(90));
        task.setId(100);
        task.setStatus(Status.DONE);
        List<Integer> subsID = task.getSubtaskIds();
        subsID.add(2);
        subsID.add(3);
        subsID.add(4);

        String result = gson.toJson(task);
        String expected = """
                {
                  "ID": 100,
                  "Type": "EPIC",
                  "Name": "name",
                  "Description": "description",
                  "Status": "DONE",
                  "StartTime": "01.01.2024 09:00",
                  "Duration": 90,
                  "SubtaskIDs": [
                    2,
                    3,
                    4
                  ]
                }""";

        assertEquals(result, expected);
    }

    @Test
    public void shouldDesearilizeTask() {
        Task task = new Task("name", "description");
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 00));
        task.setDuration(Duration.ofMinutes(90));
        task.setId(100);
        task.setStatus(Status.DONE);

        String result = gson.toJson(task);

        Task task1 = gson.fromJson(result, Task.class);

        assertEquals(task, task1);
    }

    @Test
    public void shouldDesearilizeSubtask() {
        Task task = new Subtask("name", "description", 1);
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 00));
        task.setDuration(Duration.ofMinutes(90));
        task.setId(100);
        task.setStatus(Status.DONE);

        String result = gson.toJson(task);

        Task subtask = gson.fromJson(result, Task.class);

        assertEquals(task, subtask);
    }

    @Test
    public void shouldDesearilizeEpic() {
        Epic task = new Epic("name", "description");
        task.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 00));
        task.setDuration(Duration.ofMinutes(90));
        task.setId(100);
        task.setStatus(Status.DONE);
        List<Integer> subsID = task.getSubtaskIds();
        subsID.add(2);
        subsID.add(3);
        subsID.add(4);

        String result = gson.toJson(task);

        Task epic = gson.fromJson(result, Task.class);

        assertEquals(task, epic);
    }

    @Test
    public void shouldNotDeserializeWithoutType() {

        String expected = """
                {
                  "ID": 0,
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson(expected, Task.class));

        assertEquals("Error: Illegal Arguments", e.getMessage());
    }

    @Test
    public void shouldNotDeserializeWithoutName() {

        String expected = """
                {
                  "ID": 0,
                  "Type": "TASK",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson(expected, Task.class));

        assertEquals("Error: Illegal Arguments", e.getMessage());
    }

    @Test
    public void shouldNotDeserializeWithoutDescription() {

        String expected = """
                {
                  "ID": 0,
                  "Type": "TASK",
                  "Name": "name",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson(expected, Task.class));

        assertEquals("Error: Illegal Arguments", e.getMessage());
    }

    @Test
    public void shouldDeserializeWithoutID() {

        String expected = """
                {
                  "Type": "TASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        assertDoesNotThrow(() -> gson.fromJson(expected, Task.class));
    }

    @Test
    public void shouldDeserializeWithoutStatus() {

        String expected = """
                {
                  "ID": 0,
                  "Type": "TASK",
                  "Name": "name",
                  "Description": "description",
                  "Duration": 0
                }""";

        assertDoesNotThrow(() -> gson.fromJson(expected, Task.class));
    }

    @Test
    public void shouldDeserializeWithoutDuration() {

        String expected = """
                {
                  "ID": 0,
                  "Type": "TASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW"
                }""";

        assertDoesNotThrow(() -> gson.fromJson(expected, Task.class));
    }

    @Test
    public void shouldNotDeserializeSubtaskWithoutEpicID() {

        String expected = """
                {
                  "ID": 0,
                  "Type": "SUBTASK",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> gson.fromJson(expected, Task.class));

        assertEquals("Error: Illegal Arguments", e.getMessage());
    }

    @Test
    public void shouldDeserializeEpicWithoutSubtasksID() {

        String expected = """
                {
                  "ID": 0,
                  "Type": "EPIC",
                  "Name": "name",
                  "Description": "description",
                  "Status": "NEW",
                  "Duration": 0
                }""";

        assertDoesNotThrow(() -> gson.fromJson(expected, Task.class));
    }
}



