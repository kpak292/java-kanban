package taskmanager.controller.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanager.ServerController;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    //Allowed methods for handler
    List<String> allowedMethods = List.of(
            "GET"
    );

    TaskManager manager;
    Gson gson = ServerController.getGson();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!allowedMethods.contains(exchange.getRequestMethod())) {
            writeResponse(exchange, "Error: Method not allowed", 405);
            return;
        }

        String response = gson.toJson(manager.getHistory());
        writeResponse(exchange, response, 200);
    }
}
