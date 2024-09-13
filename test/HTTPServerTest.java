import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.ServerController;
import taskmanager.controller.adapters.EpicSerializer;
import taskmanager.controller.adapters.SubtaskSerializer;
import taskmanager.controller.adapters.TaskDeserializer;
import taskmanager.controller.adapters.TaskSerializer;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Status;
import taskmanager.model.enums.Type;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HTTPServerTest {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    HttpServer server;
    TaskManager manager;
    GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(Epic.class, new EpicSerializer());

    Gson gson = builder.create();

    URI uri;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    @BeforeEach
    public void dataPreparation() throws IOException {
        server = ServerController.initialize();
        manager = ServerController.getManager();

        manager.restartCounter();
        manager.deleteAllTasks();

        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");

        manager.addTask(epic1);//1
        manager.addTask(epic2);//2

        Task task1 = new Task("Task1", "Description3");
        Task task2 = new Task("Task2", "Description4");

        LocalDateTime start1 = LocalDateTime.of(2024, 1, 1, 9, 00);
        Duration duration = Duration.ofMinutes(90);

        task1.setStartTime(start1);
        task1.setDuration(duration);

        task2.setStartTime(start1.plusHours(2));
        task2.setDuration(duration);

        manager.addTask(task1);//3
        manager.addTask(task2);//4

        Subtask subTask1 = new Subtask("Subtask1", "Description5", epic1.getId());
        Subtask subTask2 = new Subtask("Subtask2", "Description6", epic2.getId());
        Subtask subTask3 = new Subtask("Subtask3", "Description7", epic2.getId());

        subTask1.setStartTime(start1.plusHours(4));
        subTask1.setDuration(duration);

        subTask2.setStartTime(start1.plusHours(6));
        subTask2.setDuration(duration);

        subTask3.setStartTime(start1.plusHours(8));
        subTask3.setDuration(duration);

        manager.addTask(subTask1);//5
        manager.addTask(subTask2);//6
        manager.addTask(subTask3);//7
    }

    @AfterEach
    public void serverShutdown() throws IOException {
        server.stop(0);
    }

    @Test
    public void shouldRetrieveAllData() {
        uri = URI.create("http://localhost:8080/tasks/all");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getAllTasks()), response.body());
    }

    @Test
    public void shouldRetrieveTasksData() {
        uri = URI.create("http://localhost:8080/tasks/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getTasksByType(Type.TASK)), response.body());
    }

    @Test
    public void shouldRetrieveSubtasksData() {
        uri = URI.create("http://localhost:8080/tasks/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getTasksByType(Type.SUBTASK)), response.body());
    }

    @Test
    public void shouldRetrieveEpicsData() {
        uri = URI.create("http://localhost:8080/tasks/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getTasksByType(Type.EPIC)), response.body());
    }

    @Test
    public void shouldRetrieveTaskByTaskID() {
        uri = URI.create("http://localhost:8080/tasks/3");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getTaskById(3).get()), response.body());
    }

    @Test
    public void shouldNotRetrieveTaskWithIncorrectID() {
        uri = URI.create("http://localhost:8080/tasks/300");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldDeleteAll() {
        assertTrue(!manager.getAllTasks().isEmpty());

        uri = URI.create("http://localhost:8080/tasks/all");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldDeleteAllTasks() {
        assertTrue(!manager.getTasksByType(Type.TASK).isEmpty());

        uri = URI.create("http://localhost:8080/tasks/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
        assertTrue(manager.getTasksByType(Type.TASK).isEmpty());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        assertTrue(!manager.getTasksByType(Type.SUBTASK).isEmpty());

        uri = URI.create("http://localhost:8080/tasks/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
        assertTrue(manager.getTasksByType(Type.SUBTASK).isEmpty());
    }

    @Test
    public void shouldDeleteAllEpics() {
        assertTrue(!manager.getTasksByType(Type.EPIC).isEmpty());
        assertTrue(!manager.getTasksByType(Type.SUBTASK).isEmpty());

        uri = URI.create("http://localhost:8080/tasks/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
        assertTrue(manager.getTasksByType(Type.EPIC).isEmpty());
        assertTrue(manager.getTasksByType(Type.SUBTASK).isEmpty());
    }

    @Test
    public void shouldDeleteTaskByID() {
        assertTrue(manager.getTaskById(3).isPresent());

        uri = URI.create("http://localhost:8080/tasks/3");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
        assertTrue(manager.getTaskById(3).isEmpty());
    }

    @Test
    public void shouldNotDeleteTaskWithIncorrectID() {
        assertTrue(!manager.getTaskById(300).isPresent());

        uri = URI.create("http://localhost:8080/tasks/300");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldAddSimpleTask() {
        uri = URI.create("http://localhost:8080/tasks");

        String requestString = """
                   {
                   "ID": 0,
                   "Type": "TASK",
                   "Name": "TestTask",
                   "Description": "Description"
                }""";

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestString, DEFAULT_CHARSET))
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals("TestTask", task.getName());
        assertEquals(8, task.getId());
    }

    @Test
    public void shouldUpdateSubtask() {
        assertEquals(Status.NEW, manager.getTaskById(1).get().getStatus());
        assertEquals(Status.NEW, manager.getTaskById(5).get().getStatus());
        Task subtask = manager.getTaskById(5).get();
        subtask.setStatus(Status.DONE);

        uri = URI.create("http://localhost:8080/tasks");

        String requestString = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestString, DEFAULT_CHARSET))
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(Status.DONE, manager.getTaskById(1).get().getStatus());
        assertEquals(Status.DONE, manager.getTaskById(5).get().getStatus());
    }

    @Test
    public void shouldNotUpdateNotExistedtask() {
        Task task = manager.getTaskById(4).get();
        task.setId(300);

        uri = URI.create("http://localhost:8080/tasks");

        String requestString = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestString, DEFAULT_CHARSET))
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(406, response.statusCode());
        assertTrue(manager.getTaskById(300).isEmpty());
    }

    @Test
    public void shouldRetrieveCorrectHistory() {
        manager.getTaskById(3);
        manager.getTaskById(2);
        manager.getTaskById(6);

        uri = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getHistory()), response.body());
    }

    @Test
    public void shouldRetrieveCorrectPriorityList() {
        uri = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(gson.toJson(manager.getPrioritizedTasks()), response.body());
    }

    @Test
    public void shouldNotWorkWithIncorrectEndpoint1() {
        uri = URI.create("http://localhost:8080/prioritize");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldNotWorkWithIncorrectEndpoint2() {
        uri = URI.create("http://localhost:8080/tasks/every");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldNotWorkWithIncorrectEndpoint3() {
        uri = URI.create("http://localhost:8080/tasks/all");

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(uri)
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(404, response.statusCode());
    }

}
