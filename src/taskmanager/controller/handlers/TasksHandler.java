package taskmanager.controller.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanager.controller.adapters.EpicSerializer;
import taskmanager.controller.adapters.SubtaskSerializer;
import taskmanager.controller.adapters.TaskDeserializer;
import taskmanager.controller.adapters.TaskSerializer;
import taskmanager.exceptions.ManagerSaveException;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.model.enums.Endpoint;
import taskmanager.model.enums.Type;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Optional;

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //log incoming requests
        System.out.println("Received request " +
                exchange.getRequestMethod() + "/" +
                exchange.getRequestURI().getPath() +
                " " +
                LocalTime.now().toString());

        //Получение метода
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());


        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Epic.class, new EpicSerializer());

        Gson gson = builder.create();

        switch (endpoint) {
            case GET_ALL -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                String responseString = gson.toJson(manager.getAllTasks());
                writeResponse(exchange, responseString, 200);
            }
            case GET_TASKS -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                String responseString = gson.toJson(manager.getTasksByType(Type.TASK));
                writeResponse(exchange, responseString, 200);
            }
            case GET_SUBTASKS -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                String responseString = gson.toJson(manager.getTasksByType(Type.SUBTASK));
                writeResponse(exchange, responseString, 200);
            }
            case GET_EPICS -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                String responseString = gson.toJson(manager.getTasksByType(Type.EPIC));
                writeResponse(exchange, responseString, 200);
            }
            case GET_TASK_ID -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                int taskId = Integer.parseInt(exchange
                        .getRequestURI()
                        .getPath()
                        .split("/")[2]);

                Optional<Task> taskInManager = manager.getTaskById(taskId);

                if (taskInManager.isEmpty()) {
                    writeResponse(exchange, "Error: Can't find Task " + taskId, 404);
                } else {
                    writeResponse(exchange, gson.toJson(taskInManager.get()), 200);
                }
            }
            case DELETE_ALL -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain;charset=utf-8");

                try {
                    manager.deleteAllTasks();
                    writeResponse(exchange, "SUCCESS", 201);
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, e.getMessage(), 500);
                }
            }
            case DELETE_TASKS -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain;charset=utf-8");

                try {
                    manager.deleteTasksByType(Type.TASK);
                    writeResponse(exchange, "SUCCESS", 201);
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, e.getMessage(), 500);
                }
            }
            case DELETE_EPICS -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain;charset=utf-8");

                try {
                    manager.deleteTasksByType(Type.EPIC);
                    writeResponse(exchange, "SUCCESS", 201);
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, e.getMessage(), 500);
                }
            }
            case DELETE_SUBTASKS -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain;charset=utf-8");

                try {
                    manager.deleteTasksByType(Type.SUBTASK);
                    writeResponse(exchange, "SUCCESS", 201);
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, e.getMessage(), 500);
                }
            }
            case DELETE_TASK_ID -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain;charset=utf-8");

                int taskId = Integer.parseInt(exchange
                        .getRequestURI()
                        .getPath()
                        .split("/")[2]);

                try {
                    int result = manager.deleteTaskById(taskId);

                    if (result < 0) {
                        writeResponse(exchange, "Error: Can't find Task " + taskId, 404);
                    } else {
                        writeResponse(exchange, "SUCCESS", 201);
                    }
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, e.getMessage(), 500);
                }
            }
            case POST_TASK -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain;charset=utf-8");

                String request = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

                int code = 404;
                String response = "";
                Task task;
                try {
                    task = gson.fromJson(request, Task.class);
                    if (task.getId() == 0) {
                        int add = manager.addTask(task);
                        if (add < 0) throw new IllegalArgumentException("Error: Task can't be added");
                        code = 200;

                        headers.set("Content-Type", "application/json;charset=utf-8");

                        response = gson.toJson(task);
                    } else {
                        int update = manager.updateTask(task);
                        if (update < 0) throw new IllegalArgumentException("Error: Task can't be updated");
                        code = 201;
                        response = "SUCCESS";
                    }
                } catch (IllegalArgumentException e) {
                    response = e.getMessage();
                    code = 406;
                } catch (ManagerSaveException e) {
                    response = e.getMessage();
                    code = 500;
                } finally {
                    writeResponse(exchange, response, code);
                }
            }
            case GET_HISTORY -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                String response = gson.toJson(manager.getHistory());
                writeResponse(exchange, response, 200);
            }
            case GET_PRIORITIZED -> {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json;charset=utf-8");

                String response = gson.toJson(manager.getPrioritizedTasks());
                writeResponse(exchange, response, 200);
            }
            default -> writeResponse(exchange, "Error: Endpoint is not found", 404);
        }


    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (pathParts[2].matches("\\d+")) {
                return parseEndpoint(requestMethod, "task_id");
            } else {
                return parseEndpoint(requestMethod, pathParts[2]);
            }
        } else if (pathParts.length == 2 && pathParts[1].equals("tasks") && requestMethod.equals("POST")) {
            return Endpoint.POST_TASK;
        } else if (pathParts.length == 2 && pathParts[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        } else if (pathParts.length == 2 && pathParts[1].equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED;
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    private Endpoint parseEndpoint(String method, String type) {
        Endpoint result;
        try {
            result = Endpoint.valueOf(method.toUpperCase() + "_" + type.toUpperCase());
        } catch (IllegalArgumentException e) {
            result = Endpoint.UNKNOWN;
        }

        return result;
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }


}
