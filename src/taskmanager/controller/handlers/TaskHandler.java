package taskmanager.controller.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.ServerController;
import taskmanager.exceptions.ManagerSaveException;
import taskmanager.model.Task;
import taskmanager.model.enums.Endpoint;
import taskmanager.model.enums.Type;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    //Allowed methods for handler
    List<String> allowedMethods = List.of(
            "GET",
            "DELETE",
            "POST"
    );

    TaskManager manager;
    Gson gson = ServerController.getGson();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;

    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!allowedMethods.contains(exchange.getRequestMethod())) {
            writeResponse(exchange, "Error: Method not allowed", 405);
            return;
        }

        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (endpoint.equals(Endpoint.UNKNOWN)) {
            writeResponse(exchange, "Error: Endpoint is not found", 404);
            return;
        }

        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGetRequest(exchange, endpoint);
            case "DELETE" -> {
                try {
                    handleDeleteRequest(exchange, endpoint);
                } catch (ManagerSaveException e) {
                    writeResponse(exchange, e.getMessage(), 500);
                }
            }
            case "POST" -> handlePostRequest(exchange, endpoint);
            default -> writeResponse(exchange, "Error: Endpoint is not found", 404);
        }

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            if (pathParts[2].matches("\\d+")) {
                return parseEndpoint(requestMethod, "task_id");
            } else {
                return parseEndpoint(requestMethod, pathParts[2]);
            }
        } else if (pathParts.length == 2 && requestMethod.equals("POST")) {
            return Endpoint.POST_TASK;
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

    private void handleGetRequest(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint) {
            case GET_ALL -> {
                String responseString = gson.toJson(manager.getAllTasks());
                writeResponse(exchange, responseString, 200);
            }
            case GET_TASKS -> {
                String responseString = gson.toJson(manager.getTasksByType(Type.TASK));
                writeResponse(exchange, responseString, 200);
            }
            case GET_SUBTASKS -> {
                String responseString = gson.toJson(manager.getTasksByType(Type.SUBTASK));
                writeResponse(exchange, responseString, 200);
            }
            case GET_EPICS -> {
                String responseString = gson.toJson(manager.getTasksByType(Type.EPIC));
                writeResponse(exchange, responseString, 200);
            }
            case GET_TASK_ID -> {
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
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, Endpoint endpoint) throws IOException,
            ManagerSaveException {
        switch (endpoint) {
            case DELETE_ALL -> {
                manager.deleteAllTasks();
                writeResponse(exchange, "SUCCESS", 201);

            }
            case DELETE_TASKS -> {
                manager.deleteTasksByType(Type.TASK);
                writeResponse(exchange, "SUCCESS", 201);

            }
            case DELETE_EPICS -> {
                manager.deleteTasksByType(Type.EPIC);
                writeResponse(exchange, "SUCCESS", 201);
            }
            case DELETE_SUBTASKS -> {
                manager.deleteTasksByType(Type.SUBTASK);
                writeResponse(exchange, "SUCCESS", 201);
            }
            case DELETE_TASK_ID -> {
                int taskId = Integer.parseInt(exchange
                        .getRequestURI()
                        .getPath()
                        .split("/")[2]);

                int result = manager.deleteTaskById(taskId);

                if (result < 0) {
                    writeResponse(exchange, "Error: Can't find Task " + taskId, 404);
                } else {
                    writeResponse(exchange, "SUCCESS", 201);
                }
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange, Endpoint endpoint) throws IOException {
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
}
