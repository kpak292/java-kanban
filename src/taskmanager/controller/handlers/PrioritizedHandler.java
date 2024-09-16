package taskmanager.controller.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.ServerController;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    //Allowed methods for handler
    List<String> allowedMethods = List.of(
            "GET"
    );

    TaskManager manager;
    Gson gson = ServerController.getGson();

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!allowedMethods.contains(exchange.getRequestMethod())) {
            writeResponse(exchange, "Error: Method not allowed", 405);
            return;
        }

        String response = gson.toJson(manager.getPrioritizedTasks());
        writeResponse(exchange, response, 200);
    }
}
